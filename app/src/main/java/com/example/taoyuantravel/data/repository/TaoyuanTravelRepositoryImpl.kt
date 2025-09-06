package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.data.source.remote.api.ApiService
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository 介面的實作類別。
 * 負責決定從哪個資料來源 (網路或本地) 獲取資料。
 * @param apiService 透過 Hilt 注入的遠端 API 服務。
 */
class TaoyuanTravelRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TaoyuanTravelRepository {

    /**
     * 實作獲取景點列表的方法，直接呼叫 ApiService。
     */
    override suspend fun getAttractions(lang: String, page: Int): Response<ApiResponse<Attraction>> {
        return apiService.getAttractions(lang, page)
    }

    /**
     * 實作獲取最新消息列表的方法，直接呼叫 ApiService。
     */
    override suspend fun getNews(lang: String, page: Int): Response<ApiResponse<News>> {
        return apiService.getNews(lang, page)
    }
}

