package com.example.taoyuantravel.ui.home

import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.ui.model.Language

/**
 * 定義首頁所有的 UI 狀態。
 *
 * @param isLoading 是否正在載入資料。
 * @param news 最新消息的列表。
 * @param attractions 熱門景點的列表。
 * @param selectedLanguage 目前選擇的語言。
 * @param languages 所有可選的語言列表。
 * @param error 錯誤訊息，如果有的話。
 */
data class HomeState(
    val isLoading: Boolean = true,
    val news: List<News> = emptyList(),
    val attractions: List<Attraction> = emptyList(),
    val selectedLanguage: Language = Language.ZH_TW, // 直接儲存 Language 物件
    val languages: List<Language> = Language.values().toList(),
    val error: String? = null
)

