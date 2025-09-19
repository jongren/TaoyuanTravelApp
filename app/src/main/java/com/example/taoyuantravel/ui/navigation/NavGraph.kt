package com.example.taoyuantravel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taoyuantravel.ui.detail.DetailScreen
import com.example.taoyuantravel.ui.detail.DetailViewModel
import com.example.taoyuantravel.ui.home.HomeScreen
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.example.taoyuantravel.ui.planner.PlannerScreen
import com.example.taoyuantravel.ui.planner.PlannerViewModel
import com.example.taoyuantravel.ui.webview.WebViewScreen
import com.example.taoyuantravel.ui.map.MapScreen
import okhttp3.OkHttpClient

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

        composable(route = Screen.Planner.route) { backStackEntry ->
            // 使用 LocalContext.current 獲取當前上下文
            val context = LocalContext.current
            
            // 使用 CompositionLocalProvider 確保 PlannerScreen 使用正確的上下文
            CompositionLocalProvider(LocalContext provides context) {
                // 使用 androidx.lifecycle.viewmodel.compose.viewModel 而不是 hiltViewModel
                val plannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.taoyuantravel.ui.planner.PlannerViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.taoyuantravel.ui.planner.PlannerViewModel() as T
                        }
                    }
                )
                PlannerScreen(
                    navController = navController,
                    viewModel = plannerViewModel
                )
            }
        }

        composable(Screen.Map.route) {
            // 使用 LocalContext.current 獲取當前上下文
            val context = LocalContext.current
            
            // 使用 CompositionLocalProvider 確保 MapScreen 使用正確的上下文
            CompositionLocalProvider(LocalContext provides context) {
                // 使用 androidx.lifecycle.viewmodel.compose.viewModel 而不是 hiltViewModel
                val mapViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.taoyuantravel.ui.map.MapViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            // 手動創建依賴
                            val okHttpClient = OkHttpClient.Builder()
                                .addInterceptor { chain ->
                                    val original = chain.request()
                                    val requestBuilder = original.newBuilder()
                                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                    val request = requestBuilder.build()
                                    chain.proceed(request)
                                }
                                .build()
                            
                            // 創建 Gson 實例
                            val gson = com.google.gson.GsonBuilder()
                                .registerTypeAdapterFactory(com.example.taoyuantravel.data.model.ListOrObjectAdapterFactory())
                                .create()
                            
                            // 創建 Retrofit 實例
                            val retrofit = retrofit2.Retrofit.Builder()
                                .baseUrl(com.example.taoyuantravel.data.source.remote.api.ApiConstants.BASE_URL)
                                .client(okHttpClient)
                                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create(gson))
                                .build()
                            
                            // 創建 API 服務
                            val apiService = retrofit.create(com.example.taoyuantravel.data.source.remote.api.ApiService::class.java)
                            val travelRepository = com.example.taoyuantravel.data.repository.TaoyuanTravelRepositoryImpl(apiService)
                            
                            // 創建 Google Maps Retrofit 實例
                            val googleMapsRetrofit = retrofit2.Retrofit.Builder()
                                .baseUrl("https://maps.googleapis.com/maps/api/")
                                .client(okHttpClient)
                                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create(gson))
                                .build()
                            
                            val geocodingService = googleMapsRetrofit.create(com.example.taoyuantravel.data.source.remote.api.GeocodingService::class.java)
                            val geocodingRepository = com.example.taoyuantravel.data.repository.GeocodingRepositoryImpl(geocodingService)
                            
                            @Suppress("UNCHECKED_CAST")
                            return com.example.taoyuantravel.ui.map.MapViewModel(travelRepository, geocodingRepository) as T
                        }
                    }
                )
                MapScreen(
                    navController = navController,
                    viewModel = mapViewModel
                )
            }
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
    }
}

