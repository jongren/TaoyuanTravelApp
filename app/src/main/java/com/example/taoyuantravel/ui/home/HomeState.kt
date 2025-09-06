package com.example.taoyuantravel.ui.home

import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News

/**
 * 代表首頁 UI 的狀態 (State)
 * 這是一個 data class，用來存放所有需要在畫面上顯示的資料以及 UI 的狀態
 *
 * @property isLoading 是否正在載入資料
 * @property news 最新消息列表
 * @property attractions 熱門景點列表
 * @property error 錯誤訊息，如果沒有錯誤則為 null
 */
data class HomeState(
    val isLoading: Boolean = false,
    val news: List<News> = emptyList(),
    val attractions: List<Attraction> = emptyList(),
    val error: String? = null,
    val selectedLanguage: String = "zh-tw" // 預設語言為繁體中文
)
