package com.example.taoyuantravel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taoyuantravel.ui.detail.DetailScreen
import com.example.taoyuantravel.ui.home.HomeScreen

/**
 * App 的主要導航圖譜
 *
 * @param navController 導航控制器
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // 設定起始畫面為首頁
    ) {
        // 首頁路由
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // 詳情頁路由
        composable(
            route = Screen.Detail.route,
            // 定義此路由需要接收一個名為 "attractionJson" 的字串參數
            arguments = listOf(navArgument("attractionJson") { type = NavType.StringType })
        ) {
            // DetailScreen 會自動從 backStackEntry.arguments 中取得參數
            // HiltViewModel 會透過 SavedStateHandle 自動接收
            DetailScreen(navController = navController)
        }
    }
}
