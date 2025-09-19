package com.example.taoyuantravel.di

import android.content.Context
import com.example.taoyuantravel.data.language.LanguageManager
import com.example.taoyuantravel.data.preferences.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 語系管理相關的 Hilt 模組
 */
@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(
        preferencesManager: PreferencesManager,
        @ApplicationContext context: Context
    ): LanguageManager {
        return LanguageManager(preferencesManager, context)
    }
}