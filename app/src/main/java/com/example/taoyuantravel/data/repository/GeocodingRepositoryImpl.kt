package com.example.taoyuantravel.data.repository

import android.util.Log
import com.example.taoyuantravel.BuildConfig
import com.example.taoyuantravel.data.source.remote.api.GeocodingResponse
import com.example.taoyuantravel.data.source.remote.api.GeocodingService
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

/**
 * 地理編碼 Repository 實作類別
 * 負責實際執行地址轉換為經緯度座標的操作
 */
class GeocodingRepositoryImpl @Inject constructor(
    private val geocodingService: GeocodingService
) : GeocodingRepository {

    companion object {
        private const val TAG = "GeocodingRepository"
        private const val DELAY_BETWEEN_REQUESTS = 100L // API 請求間隔，避免超過限制
    }

    override suspend fun geocodeAddress(address: String): Response<GeocodingResponse> {
        return try {
            val apiKey = BuildConfig.GOOGLE_MAPS_GEOCODING_API_KEY
            if (apiKey.isEmpty()) {
                Log.e(TAG, "Google Maps Geocoding API Key 未設定")
                throw IllegalStateException("Google Maps Geocoding API Key 未設定")
            }

            geocodingService.geocodeAddress(address, apiKey)
        } catch (e: Exception) {
            Log.e(TAG, "地理編碼失敗: $address", e)
            throw e
        }
    }

    override suspend fun batchGeocodeAddresses(addresses: List<String>): Map<String, Pair<Double, Double>?> {
        val result = mutableMapOf<String, Pair<Double, Double>?>()
        
        for (address in addresses) {
            try {
                val response = geocodeAddress(address)
                if (response.isSuccessful && response.body()?.status == "OK") {
                    val location = response.body()?.results?.firstOrNull()?.geometry?.location
                    if (location != null) {
                        result[address] = Pair(location.lat, location.lng)
                    } else {
                        result[address] = null
                    }
                } else {
                    result[address] = null
                }
                
                // 避免超過 API 請求限制
                delay(DELAY_BETWEEN_REQUESTS)
                
            } catch (e: Exception) {
                result[address] = null
                Log.e(TAG, "地理編碼異常: $address", e)
            }
        }
        
        return result
    }
}