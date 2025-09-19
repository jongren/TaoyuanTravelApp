package com.example.taoyuantravel.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 應用程式偏好設定管理器
 * 使用 DataStore 進行持久化儲存
 */
@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
        
        // 偏好設定鍵值
        private val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")
    }

    /**
     * 儲存語系代碼
     */
    suspend fun saveLanguageCode(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE_KEY] = languageCode
        }
    }

    /**
     * 取得語系代碼
     * @return Flow<String> 語系代碼流，預設為繁體中文
     */
    fun getLanguageCode(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_CODE_KEY] ?: "zh-tw" // 預設為繁體中文
        }
    }

    /**
     * 清除所有偏好設定
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}