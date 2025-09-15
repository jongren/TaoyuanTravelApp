package com.example.taoyuantravel.ui.planner

/**
 * 行程項目數據類
 * 
 * @param time 時間（例如："09:00"）
 * @param activity 活動名稱
 * @param location 地點名稱
 * @param description 活動描述
 */
data class ItineraryItem(
    val time: String,
    val activity: String,
    val location: String,
    val description: String = ""
)

/**
 * 定義行程規劃器所有的 UI 狀態。
 *
 * @param isLoading 是否正在載入（生成行程中）。
 * @param userInput 使用者輸入的偏好文字。
 * @param itinerary 生成的行程列表。
 * @param error 錯誤訊息，如果有的話。
 */
data class PlannerState(
    val isLoading: Boolean = false,
    val userInput: String = "",
    val itinerary: List<ItineraryItem> = emptyList(),
    val error: String? = null
)