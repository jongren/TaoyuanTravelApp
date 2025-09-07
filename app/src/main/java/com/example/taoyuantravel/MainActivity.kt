package com.example.taoyuantravel

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.example.taoyuantravel.ui.navigation.NavGraph
import com.example.taoyuantravel.ui.theme.TaoyuanTravelTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val state by homeViewModel.state.collectAsState()

            // 使用我們自訂的 Wrapper Composable 來應用語言設定
            LocaleWrapper(locale = state.selectedLanguage.locale) {
                TaoyuanTravelTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        // 將共享的 viewModel 實例傳遞給 NavGraph
                        NavGraph(
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * 一個 Composable Wrapper，
 * 它會根據傳入的 Locale 創建一個新的 Context，
 * 並透過 CompositionLocalProvider 將其提供給其下的所有 Composable。
 * 這是確保 stringResource 能正確響應語言切換的最穩定方法。
 *
 * @param locale 要應用的地區語言設定。
 * @param content 需要應用此語言設定的 Composable 內容。
 */
@Composable
fun LocaleWrapper(
    locale: Locale,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // 只有當 locale 改變時，才重新計算 configuration 和 localizedContext
    val (configuration, localizedContext) = remember(locale) {
        val conf = Configuration(context.resources.configuration)
        conf.setLocale(locale)
        val ctx = context.createConfigurationContext(conf)
        Pair(conf, ctx)
    }

    // 將帶有新語言設定的 Context 提供給 Composable 樹
    CompositionLocalProvider(LocalContext provides localizedContext) {
        content()
    }
}

