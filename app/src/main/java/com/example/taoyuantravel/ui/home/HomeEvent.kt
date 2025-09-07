package com.example.taoyuantravel.ui.home

/**
 * 定義所有可以從 UI 層發送到 ViewModel 的事件。
 */
sealed interface HomeEvent {
    /**
     * 表示需要載入資料的事件。
     */
    data object LoadData : HomeEvent

    /**
     * 表示使用者選擇了新的語言。
     * @param langCode 新語言的代碼 (例如 "en", "ja")。
     */
    data class ChangeLanguage(val langCode: String) : HomeEvent
}

