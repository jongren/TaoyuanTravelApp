package com.example.taoyuantravel.ui.planner

/**
 * 定義所有可以從 UI 層發送到 PlannerViewModel 的事件。
 */
sealed interface PlannerEvent {
    /**
     * 當使用者輸入文字時觸發
     * @param text 使用者輸入的文字
     */
    data class OnUserInputChanged(val text: String) : PlannerEvent

    /**
     * 當使用者點擊按鈕時觸發
     */
    data object GenerateItinerary : PlannerEvent
}