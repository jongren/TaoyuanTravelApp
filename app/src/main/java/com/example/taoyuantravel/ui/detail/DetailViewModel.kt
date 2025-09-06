package com.example.taoyuantravel.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.taoyuantravel.data.model.Attraction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle // Hilt 會自動注入這個物件，用來存取導航參數
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        // 從 savedStateHandle 中取得名為 "attractionJson" 的參數
        savedStateHandle.get<String>("attractionJson")?.let { attractionJson ->
            try {
                // 使用 Gson 將 JSON 字串反序列化回 Attraction 物件
                val attraction = Gson().fromJson(attractionJson, Attraction::class.java)
                // 更新 UI 狀態
                _state.update { it.copy(attraction = attraction) }
            } catch (e: Exception) {
                // 如果解析失敗，可以在這裡處理錯誤
            }
        }
    }
}

