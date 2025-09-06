package com.example.taoyuantravel.data.source.remote.api

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 定義所有與桃園觀光旅遊網 API 的溝通方法
 */
interface ApiService {

    /**
     * 取得所有景點資料
     * @param lang 語系 (zh-tw, en, ja, ko, es, th, vi)
     * @param page 頁碼
     * @return 包含景點列表的 API 回應
     */
    @Headers("Accept: application/json")
    @GET("{lang}/Attractions/All")
    suspend fun getAttractions(
        @Path("lang") lang: String,
        @Query("page") page: Int = 1
    ): ApiResponse<Attraction>

    /**
     * 取得所有最新消息
     * @param lang 語系
     * @param page 頁碼
     * @return 包含最新消息列表的 API 回應
     */
    @Headers("Accept: application/json")
    @GET("{lang}/News/All")
    suspend fun getNews(
        @Path("lang") lang: String,
        @Query("page") page: Int = 1
    ): ApiResponse<News>
}
