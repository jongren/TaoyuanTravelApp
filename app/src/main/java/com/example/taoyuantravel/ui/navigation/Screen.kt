package com.example.taoyuantravel.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Planner : Screen("planner")
    object Map : Screen("map")

    // 1. 定義帶有參數的基礎路徑
    object Detail : Screen("detail/{attractionJson}") {
        // 2. 建立一個輔助函式，用實際的 json 字串替換掉佔位符
        fun createRoute(attractionJson: String): String {
            return "detail/$attractionJson"
        }
    }

    object WebView : Screen("webView/{url}/{title}") {
        fun createRoute(url: String, title: String? = null): String {
            val encodedTitle = title?.let { java.net.URLEncoder.encode(it, "UTF-8") } ?: "none"
            return "webView/$url/$encodedTitle"
        }
    }
}

