package com.example.taoyuantravel.ui.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.taoyuantravel.data.language.LanguageManager
import com.example.taoyuantravel.ui.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 全局語系狀態的 CompositionLocal
 */
val LocalLanguage = compositionLocalOf<Language> { Language.TRADITIONAL_CHINESE }

/**
 * 語系狀態 ViewModel
 */
@HiltViewModel
class LanguageStateViewModel @Inject constructor(
    private val languageManager: LanguageManager
) : ViewModel() {
    
    val currentLanguage = languageManager.getCurrentLanguage()
    
    suspend fun setLanguage(language: Language) {
        languageManager.setLanguage(language)
    }
}

/**
 * 語系狀態提供者
 * 提供全局的語系狀態給整個應用程式
 */
@Composable
fun LanguageProvider(
    content: @Composable () -> Unit
) {
    val languageViewModel: LanguageStateViewModel = hiltViewModel()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState(initial = Language.TRADITIONAL_CHINESE)
    
    CompositionLocalProvider(
        LocalLanguage provides currentLanguage
    ) {
        content()
    }
}