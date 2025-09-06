package com.example.taoyuantravel.di

import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 綁定 TaoyuanTravelRepository 的實作
     * 當需要 TaoyuanTravelRepository 介面時，Hilt 會提供 TaoyuanTravelRepositoryImpl 的實例
     */
    @Binds
    @Singleton
    abstract fun bindTaoyuanTravelRepository(
        taoyuanTravelRepositoryImpl: TaoyuanTravelRepositoryImpl
    ): TaoyuanTravelRepository
}
