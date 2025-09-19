package com.example.taoyuantravel.ui.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taoyuantravel.ui.model.Language

/**
 * 在 Composable 中使用當前語系
 */
@Composable
fun useCurrentLanguage(): Language {
    return LocalLanguage.current
}

/**
 * 在 Composable 中使用語系變更功能
 */
@Composable
fun useLanguageManager(): LanguageStateViewModel {
    return hiltViewModel()
}

/**
 * 監聽語系變更的 Hook
 * @param onLanguageChanged 語系變更時的回調函數
 */
@Composable
fun useLanguageEffect(onLanguageChanged: (Language) -> Unit) {
    val currentLanguage = useCurrentLanguage()
    
    LaunchedEffect(currentLanguage) {
        onLanguageChanged(currentLanguage)
    }
}