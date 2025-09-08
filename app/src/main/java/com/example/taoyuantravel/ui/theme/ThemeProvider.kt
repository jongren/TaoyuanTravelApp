package com.example.taoyuantravel.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

// CompositionLocal for theme management
val LocalThemeViewModel = compositionLocalOf<ThemeViewModel> {
    error("ThemeViewModel not provided")
}

@Composable
fun ThemeProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = remember {
        ThemeViewModel(context)
    }
    
    CompositionLocalProvider(
        LocalThemeViewModel provides themeViewModel
    ) {
        TaoyuanTravelTheme(
            darkTheme = themeViewModel.isDarkMode,
            dynamicColor = false, // 使用自定義顏色而非動態顏色
            content = content
        )
    }
}