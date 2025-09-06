package com.example.taoyuantravel.ui.detail

import com.example.taoyuantravel.data.model.Attraction

/**
 * 景點詳情頁的 UI 狀態
 *
 * @property attraction 當前顯示的景點物件，可能為 null 直到資料載入完成
 */
data class DetailState(
    val attraction: Attraction? = null
)
