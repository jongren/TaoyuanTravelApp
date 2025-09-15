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
        loadAttractionData(savedStateHandle)
    }

    /**
     * 載入景點資料
     */
    private fun loadAttractionData(savedStateHandle: SavedStateHandle) {
        // 從 savedStateHandle 中取得名為 "attractionJson" 的參數
        val attractionJson = savedStateHandle.get<String>("attractionJson")
        
        if (attractionJson == null) {
            _state.update { 
                it.copy(
                    isLoading = false, 
                    error = "無法取得景點資料"
                ) 
            }
            return
        }

        runCatching {
            // 使用 Gson 將 JSON 字串反序列化回 Attraction 物件
            Gson().fromJson(attractionJson, Attraction::class.java)
        }.onSuccess { attraction ->
            _state.update { 
                it.copy(
                    attraction = attraction,
                    isLoading = false,
                    error = null
                ) 
            }
        }.onFailure { exception ->
            Log.e("DetailViewModel", "解析景點資料失敗", exception)
            _state.update { 
                it.copy(
                    isLoading = false,
                    error = "解析景點資料失敗：${exception.message}"
                ) 
            }
        }
    }
}

