package com.example.taoyuantravel.ui.detail

import com.example.taoyuantravel.data.model.Attraction

/**
 * 景點詳情頁的 UI 狀態
 *
 * @property attraction 當前顯示的景點物件，可能為 null 直到資料載入完成
 * @property isLoading 是否正在載入資料
 * @property error 錯誤訊息，null 表示沒有錯誤
 */
data class DetailState(
    val attraction: Attraction? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
