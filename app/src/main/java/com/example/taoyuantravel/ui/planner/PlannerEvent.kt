package com.example.taoyuantravel.ui.planner

/**
 * 定義所有可以從 UI 層發送到 PlannerViewModel 的事件。
 */
sealed interface PlannerEvent {
    /**
     * 表示使用者輸入偏好文字的事件。
     * @param input 使用者輸入的偏好文字
     */
    data class UpdateUserInput(val input: String) : PlannerEvent

    /**
     * 表示使用者點擊生成行程按鈕的事件。
     */
    data object GenerateItinerary : PlannerEvent

    /**
     * 表示清除錯誤訊息的事件。
     */
    data object ClearError : PlannerEvent

    /**
     * 表示重置所有狀態的事件。
     */
    data object Reset : PlannerEvent
}