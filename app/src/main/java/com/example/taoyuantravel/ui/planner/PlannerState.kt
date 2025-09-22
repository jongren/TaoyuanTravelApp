package com.example.taoyuantravel.ui.planner

import com.example.taoyuantravel.data.model.ItineraryResponse

/**
 * 定義行程規劃器所有的 UI 狀態。
 *
 * @param isLoading 表示是否正在向 AI 請求資料
 * @param result 用來儲存 AI 回傳的行程規劃結果
 * @param error 儲存錯誤訊息
 */
data class PlannerState(
    val isLoading: Boolean = false,
    val result: ItineraryResponse? = null,
    val error: String? = null
)