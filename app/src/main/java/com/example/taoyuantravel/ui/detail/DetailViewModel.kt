package com.example.taoyuantravel.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.taoyuantravel.data.model.Attraction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    // SavedStateHandle 會自動接收來自 Navigation 的參數
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    init {
        // 從 SavedStateHandle 中取出名為 "attractionJson" 的參數
        val attractionJson = savedStateHandle.get<String>("attractionJson")

        if (attractionJson != null) {
            // 使用 Gson 將 JSON 字串反序列化回 Attraction 物件
            val attraction = Gson().fromJson(attractionJson, Attraction::class.java)
            // 更新 UI 狀態
            _state.update { it.copy(attraction = attraction) }
        }
    }
}
