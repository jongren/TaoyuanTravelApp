package com.example.taoyuantravel.ui.home

/**
 * 代表使用者在首頁上可以觸發的所有操作 (Intent/Event)
 * 這是一個 sealed interface，可以確保所有可能的事件都在此定義
 */
sealed interface HomeEvent {
    /**
     * 初始化載入資料的事件
     */
    data object LoadData : HomeEvent

    /**
     * 切換語系的事件
     * @param lang 目標語系代碼
     */
    data class ChangeLanguage(val lang: String) : HomeEvent
}
