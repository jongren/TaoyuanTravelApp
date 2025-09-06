package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News

/**
 * 旅遊資料倉儲的介面
 * 定義了資料層需要提供給業務邏輯層的功能
 */
interface TaoyuanTravelRepository {

    /**
     * 獲取景點列表
     * @param lang 語系
     * @param page 頁碼
     * @return 景點資料的 API 回應
     */
    suspend fun getAttractions(lang: String, page: Int): ApiResponse<Attraction>

    /**
     * 獲取最新消息列表
     * @param lang 語系
     * @param page 頁碼
     * @return 最新消息資料的 API 回應
     */
    suspend fun getNews(lang: String, page: Int): ApiResponse<News>
}
