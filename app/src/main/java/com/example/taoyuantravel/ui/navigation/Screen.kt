package com.example.taoyuantravel.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 定義 App 內所有可導航的畫面 (Screen)
 *
 * 使用 sealed class 可以確保所有路由都在此處定義，增加型別安全性
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")

    // 詳情頁路由，需要一個 attractionJson 參數
    // 我們將景點物件序列化成 JSON 字串來傳遞
    data object Detail : Screen("detail/{attractionJson}") {
        fun createRoute(attractionJson: String): String {
            // 對 JSON 字串進行 URL 編碼，避免特殊字元影響路由解析
            val encodedJson = URLEncoder.encode(attractionJson, StandardCharsets.UTF_8.toString())
            return "detail/$encodedJson"
        }
    }
}
