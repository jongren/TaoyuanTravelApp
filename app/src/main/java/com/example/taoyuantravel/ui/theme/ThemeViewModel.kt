package com.example.taoyuantravel.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ThemeViewModel(private val context: Context) : ViewModel() {
    
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
    }
    
    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var isDarkMode by mutableStateOf(false)
        private set
    
    init {
        loadThemePreference()
    }
    
    private fun loadThemePreference() {
        isDarkMode = sharedPrefs.getBoolean(KEY_DARK_MODE, false)
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            isDarkMode = !isDarkMode
            saveThemePreference()
        }
    }
    
    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            isDarkMode = enabled
            saveThemePreference()
        }
    }
    
    private fun saveThemePreference() {
        sharedPrefs.edit()
            .putBoolean(KEY_DARK_MODE, isDarkMode)
            .apply()
    }
}