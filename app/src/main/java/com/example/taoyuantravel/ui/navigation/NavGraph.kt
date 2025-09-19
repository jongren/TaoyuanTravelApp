package com.example.taoyuantravel.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
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
import com.example.taoyuantravel.data.language.LanguageManager
import com.example.taoyuantravel.data.model.ListOrObjectAdapterFactory
import com.example.taoyuantravel.data.preferences.PreferencesManager
import com.example.taoyuantravel.data.repository.GeocodingRepositoryImpl
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepositoryImpl
import com.example.taoyuantravel.data.source.remote.api.ApiConstants
import com.example.taoyuantravel.data.source.remote.api.ApiService
import com.example.taoyuantravel.data.source.remote.api.GeocodingService
import com.example.taoyuantravel.ui.detail.DetailScreen
import com.example.taoyuantravel.ui.detail.DetailViewModel
import com.example.taoyuantravel.ui.home.HomeScreen
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.example.taoyuantravel.ui.map.MapScreen
import com.example.taoyuantravel.ui.map.MapViewModel
import com.example.taoyuantravel.ui.planner.PlannerScreen
import com.example.taoyuantravel.ui.planner.PlannerViewModel
import com.example.taoyuantravel.ui.webview.WebViewScreen
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            val context = LocalContext.current

            // 使用 CompositionLocalProvider 確保 PlannerScreen 使用正確的上下文
            CompositionLocalProvider(LocalContext provides context) {
                val plannerViewModel = viewModel<PlannerViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return PlannerViewModel() as T
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
            val context = LocalContext.current
            val mapViewModel = remember {
                // 手動創建依賴
                val gson = GsonBuilder()
                    .registerTypeAdapterFactory(ListOrObjectAdapterFactory())
                    .create()
                
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                    .build()
                
                val taoyuanRetrofit = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                
                val googleMapsRetrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                
                val apiService = taoyuanRetrofit.create(ApiService::class.java)
                val geocodingService = googleMapsRetrofit.create(GeocodingService::class.java)
                
                val travelRepository = TaoyuanTravelRepositoryImpl(apiService)
                val geocodingRepository = GeocodingRepositoryImpl(geocodingService)
                val preferencesManager = PreferencesManager(context)
                val languageManager = LanguageManager(preferencesManager, context)
                
                MapViewModel(travelRepository, geocodingRepository, languageManager)
            }
            
            MapScreen(
                navController = navController,
                viewModel = mapViewModel
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("attractionJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val attractionJson = backStackEntry.arguments?.getString("attractionJson") ?: ""
            val context = LocalContext.current

            CompositionLocalProvider(LocalContext provides context) {
                val detailViewModel = viewModel<DetailViewModel>(
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
                DetailScreen(
                    navController = navController,
                    viewModel = detailViewModel
                )
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

