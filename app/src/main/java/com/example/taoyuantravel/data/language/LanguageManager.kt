package com.example.taoyuantravel.data.language

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import com.example.taoyuantravel.data.preferences.PreferencesManager
import com.example.taoyuantravel.ui.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 全局語系管理器
 * 負責統一處理語系切換邏輯
 */
@Singleton
class LanguageManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val context: Context
) {
    
    /**
     * 取得當前語系設定
     */
    fun getCurrentLanguage(): Flow<Language> {
        return preferencesManager.getLanguageCode().map { code ->
            Language.fromCode(code)
        }
    }

    /**
     * 設定語系
     * @param language 要設定的語系
     */
    suspend fun setLanguage(language: Language) {
        // 儲存到偏好設定
        preferencesManager.saveLanguageCode(language.code)
        
        // 立即應用語系變更
        applyLanguageToContext(language)
    }

    /**
     * 應用語系到 Context
     * @param language 要應用的語系
     */
    private fun applyLanguageToContext(language: Language) {
        // 直接使用 Language 枚舉中預定義的 locale，避免重複解析
        val locale = language.locale
        val configuration = Configuration(context.resources.configuration)
        
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    /**
     * 取得系統預設語系
     */
    fun getSystemDefaultLanguage(): Language {
        val systemLocale = context.resources.configuration.locales[0]
        val languageCode = "${systemLocale.language}-${systemLocale.country}".lowercase()
        
        return Language.values().find { it.code == languageCode } 
            ?: Language.TRADITIONAL_CHINESE // 預設為繁體中文
    }

    /**
     * 初始化語系設定
     * 如果是第一次啟動，使用系統預設語系
     */
    suspend fun initializeLanguage() {
        // 取得當前儲存的語系設定並應用
        val languageCode = preferencesManager.getLanguageCode().first()
        val language = Language.fromCode(languageCode)
        applyLanguageToContext(language)
    }
}