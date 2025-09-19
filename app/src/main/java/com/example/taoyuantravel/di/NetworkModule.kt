package com.example.taoyuantravel.di

import com.example.taoyuantravel.data.model.ListOrObjectAdapterFactory
import com.example.taoyuantravel.data.repository.GeocodingRepository
import com.example.taoyuantravel.data.repository.GeocodingRepositoryImpl
import com.example.taoyuantravel.data.source.remote.api.ApiConstants
import com.example.taoyuantravel.data.source.remote.api.ApiService
import com.example.taoyuantravel.data.source.remote.api.GeocodingService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 用於區分不同的 Retrofit 實例
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TaoyuanApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleMapsApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // 創建一個包含我們自訂 Adapter 的 Gson 實例
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(ListOrObjectAdapterFactory())
            .create()

    @Provides
    @Singleton
    @TaoyuanApi
    fun provideTaoyuanRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 使用自訂的 Gson
            .build()
    }

    @Provides
    @Singleton
    @GoogleMapsApi
    fun provideGoogleMapsRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(@TaoyuanApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingService(@GoogleMapsApi retrofit: Retrofit): GeocodingService {
        return retrofit.create(GeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(geocodingService: GeocodingService): GeocodingRepository {
        return GeocodingRepositoryImpl(geocodingService)
    }
}