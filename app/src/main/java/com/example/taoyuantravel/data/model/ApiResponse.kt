package com.example.taoyuantravel.data.model

/**
 * 通用的 API 回應結構
 * @param T 資料的具體類型 (例如 Attraction 或 News)
 * @property total 總資料筆數
 * @property data 資料列表
 */
data class ApiResponse<T>(
    val total: Int,
    val data: List<T>
)
