package com.example.taoyuantravel.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.taoyuantravel.data.model.Attraction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * 詳情頁ViewModel，管理景點詳情的UI狀態
 * 
 * @param savedStateHandle 用於存取導航參數的狀態處理器
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        // 從 savedStateHandle 中取得名為 "attractionJson" 的參數
        savedStateHandle.get<String>("attractionJson")?.let { attractionJson ->
            try {
                // 使用 Gson 將 JSON 字串反序列化回 Attraction 物件
                val attraction = Gson().fromJson(attractionJson, Attraction::class.java)
/*
                // 輸出API取得的資料log
                val prettyGson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = prettyGson.toJson(attraction)
                Log.d("DetailViewModel", "景點資料: \n$prettyJson")

                // 輸出電話和官方網站資訊的log
                val phoneLink = attraction.links?.items?.find { it.subject.contains("電話", ignoreCase = true) ||
                                                              it.subject.contains("Tel", ignoreCase = true) ||
                                                              it.subject.contains("Phone", ignoreCase = true) }
                val websiteLink = attraction.links?.items?.find { it.subject.contains("網站", ignoreCase = true) ||
                                                               it.subject.contains("網址", ignoreCase = true) ||
                                                               it.subject.contains("官網", ignoreCase = true) ||
                                                               it.subject.contains("Website", ignoreCase = true) ||
                                                               it.subject.contains("Site", ignoreCase = true) }

                Log.d("DetailViewModel", "電話: ${phoneLink?.src ?: "無電話資訊"}")
                Log.d("DetailViewModel", "官方網站: ${websiteLink?.src ?: "無官方網站資訊"}")
 */
                // 更新 UI 狀態
                _state.update { it.copy(attraction = attraction) }
            } catch (e: Exception) {
                // 如果解析失敗，可以在這裡處理錯誤
            }
        }
    }
}

