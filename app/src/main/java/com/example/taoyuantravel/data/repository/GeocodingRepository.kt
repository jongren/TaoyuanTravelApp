package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.source.remote.api.GeocodingResponse
import retrofit2.Response

/**
 * 地理編碼 Repository 介面
 * 負責處理地址轉換為經緯度座標的相關操作
 */
interface GeocodingRepository {

    /**
     * 將地址轉換為經緯度座標
     * @param address 要轉換的地址
     * @return 地理編碼回應，包含經緯度資訊
     */
    suspend fun geocodeAddress(address: String): Response<GeocodingResponse>

    /**
     * 批次處理多個地址的地理編碼
     * @param addresses 地址列表
     * @return 地址與座標的對應 Map
     */
    suspend fun batchGeocodeAddresses(addresses: List<String>): Map<String, Pair<Double, Double>?>
}