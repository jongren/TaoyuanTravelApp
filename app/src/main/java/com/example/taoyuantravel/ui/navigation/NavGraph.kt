package com.example.taoyuantravel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taoyuantravel.ui.detail.DetailScreen
import com.example.taoyuantravel.ui.detail.DetailViewModel
import com.example.taoyuantravel.ui.home.HomeScreen
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.example.taoyuantravel.ui.settings.SettingsScreen
import com.example.taoyuantravel.ui.webview.WebViewScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel
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
        ) { backStackEntry ->
            val attractionJson = backStackEntry.arguments?.getString("attractionJson") ?: ""
            
            // 使用 LocalContext.current 獲取當前上下文
            val context = LocalContext.current
            
            // 使用 CompositionLocalProvider 確保 DetailScreen 使用正確的上下文
            CompositionLocalProvider(LocalContext provides context) {
                // 使用 androidx.lifecycle.viewmodel.compose.viewModel 而不是 hiltViewModel
                val detailViewModel = androidx.lifecycle.viewmodel.compose.viewModel<DetailViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            val savedStateHandle = SavedStateHandle().apply {
                                set("attractionJson", attractionJson)
                            }
                            @Suppress("UNCHECKED_CAST")
                            return DetailViewModel(savedStateHandle) as T
                        }
                    }
                )
                DetailScreen(navController = navController, viewModel = detailViewModel)
            }
        }

        composable(
            route = Screen.WebView.route,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 從路由中取出編碼後的 URL 和標題
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val encodedTitle = backStackEntry.arguments?.getString("title") ?: "none"
            
            // 解碼標題
            val title = if (encodedTitle != "none") {
                java.net.URLDecoder.decode(encodedTitle, "UTF-8")
            } else {
                null
            }
            
            WebViewScreen(navController = navController, encodedUrl = encodedUrl, title = title)
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

