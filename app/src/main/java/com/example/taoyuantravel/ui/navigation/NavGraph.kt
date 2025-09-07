package com.example.taoyuantravel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taoyuantravel.ui.detail.DetailScreen
import com.example.taoyuantravel.ui.home.HomeScreen
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.example.taoyuantravel.ui.webview.WebViewScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel // 接收從 MainActivity 傳來的共享 ViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            // 將共享的 ViewModel 傳遞給 HomeScreen
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("attractionJson") { type = NavType.StringType })
        ) {
            // DetailViewModel 會自動從 SavedStateHandle 取得 attractionJson 參數
            DetailScreen(navController = navController)
        }

        composable(
            route = Screen.WebView.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            // 從路由中取出編碼後的 URL
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            // 將參數傳遞給 WebViewScreen
            WebViewScreen(navController = navController, encodedUrl = encodedUrl)
        }
    }
}

