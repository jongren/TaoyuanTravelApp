package com.example.taoyuantravel.ui.detail

/**
 * 景點詳情頁的 UI 事件
 * 目前詳情頁沒有使用者可以觸發的複雜操作，因此是空的
 */
sealed interface DetailEvent {
    // 例如：未來可以加入收藏景點的事件
    // data class AddToFavorite(val attractionId: Int) : DetailEvent
}
