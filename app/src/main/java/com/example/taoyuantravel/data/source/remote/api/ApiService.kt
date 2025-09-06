package com.example.taoyuantravel.data.source.remote.api

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 定義所有與桃園觀光旅遊網 API 溝通的方法
 */
interface ApiService {

    /**
     * 獲取景點列表
     * @param lang 語系
     * @param page 頁碼
     * @return 包含景點列表的回應
     */
    @Headers("Accept: application/json")
    @GET("{lang}/Travel/Attraction") // 修正：移除 Attractions 最後的 s
    suspend fun getAttractions(
        @Path("lang") lang: String,
        @Query("page") page: Int
    ): Response<ApiResponse<Attraction>>

    /**
     * 獲取最新消息列表
     * @param lang 語系
     * @param page 頁碼
     * @return 包含最新消息列表的回應
     */
    @Headers("Accept: application/json")
    @GET("{lang}/Event/News")
    suspend fun getNews(
        @Path("lang") lang: String,
        @Query("page") page: Int
    ): Response<ApiResponse<News>>
}

