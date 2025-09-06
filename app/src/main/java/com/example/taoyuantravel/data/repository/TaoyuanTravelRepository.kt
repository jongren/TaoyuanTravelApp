package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import retrofit2.Response

/**
 * Repository 的公開介面，定義了 ViewModel 可以存取資料的方法。
 * 這是資料層的唯一入口點。
 */
interface TaoyuanTravelRepository {

    /**
     * 獲取景點列表
     * @param lang 語系
     * @param page 頁碼
     * @return 景點資料的 API 回應
     */
    suspend fun getAttractions(lang: String, page: Int): Response<ApiResponse<Attraction>>

    /**
     * 獲取最新消息列表
     * @param lang 語系
     * @param page 頁碼
     * @return 最新消息資料的 API 回應
     */
    suspend fun getNews(lang: String, page: Int): Response<ApiResponse<News>>
}
