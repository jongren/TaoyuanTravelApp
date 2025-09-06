package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.data.source.remote.api.ApiService
import javax.inject.Inject

/**
 * TaoyuanTravelRepository 的實作
 * @param apiService 遠端 API 服務，由 Hilt 注入
 */
class TaoyuanTravelRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TaoyuanTravelRepository {

    /**
     * 呼叫 ApiService 來獲取景點資料
     */
    override suspend fun getAttractions(lang: String, page: Int): ApiResponse<Attraction> {
        return apiService.getAttractions(lang, page)
    }

    /**
     * 呼叫 ApiService 來獲取最新消息資料
     */
    override suspend fun getNews(lang: String, page: Int): ApiResponse<News> {
        return apiService.getNews(lang, page)
    }
}
