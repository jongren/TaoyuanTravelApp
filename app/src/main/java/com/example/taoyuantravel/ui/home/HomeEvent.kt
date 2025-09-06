package com.example.taoyuantravel.ui.home

/**
 * 定義所有可以從 UI 發送到 ViewModel 的事件
 */
sealed interface HomeEvent {
    // 當使用者在下拉選單中選擇新的語言時觸發
    data class ChangeLanguage(val langCode: String) : HomeEvent
}

