package com.example.taoyuantravel.data.source.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Google Geocoding API 服務介面
 * 用於將地址轉換為經緯度座標
 */
interface GeocodingService {

    /**
     * 地理編碼：將地址轉換為經緯度座標
     * @param address 要查詢的地址
     * @param apiKey Google Maps API Key
     * @param language 回應語言 (預設為繁體中文)
     * @return 地理編碼回應
     */
    @GET("geocode/json")
    suspend fun geocodeAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "zh-TW"
    ): Response<GeocodingResponse>
}

/**
 * Google Geocoding API 回應資料結構
 */
data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

/**
 * 地理編碼結果
 */
data class GeocodingResult(
    val geometry: Geometry,
    val formatted_address: String,
    val address_components: List<AddressComponent>
)

/**
 * 幾何資訊
 */
data class Geometry(
    val location: Location,
    val location_type: String
)

/**
 * 位置座標
 */
data class Location(
    val lat: Double,
    val lng: Double
)

/**
 * 地址組成部分
 */
data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)