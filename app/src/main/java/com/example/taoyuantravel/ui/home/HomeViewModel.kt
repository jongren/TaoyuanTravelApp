package com.example.taoyuantravel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.example.taoyuantravel.ui.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaoyuanTravelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // ViewModel 初始化時載入預設語言的資料
        loadData()
    }

    /**
     * 處理從 UI 傳來的事件
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            // 處理語言變更事件
            is HomeEvent.ChangeLanguage -> {
                // 根據傳入的 langCode 字串找到對應的 Language 物件
                val newLanguage = Language.values().find { it.code == event.langCode } ?: return

                // 如果選擇的語言與當前語言不同，才更新狀態並重新載入資料
                if (newLanguage != _state.value.selectedLanguage) {
                    _state.update { it.copy(selectedLanguage = newLanguage) }
                    loadData()
                }
            }
        }
    }

    /**
     * 根據當前的 selectedLanguage 從 Repository 載入資料
     */
    private fun loadData() {
        viewModelScope.launch {
            // 開始載入，顯示 로딩 중... 指示器
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val langCode = _state.value.selectedLanguage.code
                // 同時發送最新消息和熱門景點的 API 請求
                val newsResponse = repository.getNews(langCode, 1)
                val attractionsResponse = repository.getAttractions(langCode, 1)

                // 檢查兩個請求是否都成功
                if (newsResponse.isSuccessful && attractionsResponse.isSuccessful) {
                    // 從 Response Body 中安全地取出資料列表
                    val news = newsResponse.body()?.infos?.data ?: emptyList()
                    val attractions = attractionsResponse.body()?.infos?.data ?: emptyList()

                    // 更新狀態，隱藏 로딩 중... 指示器並顯示資料
                    _state.update {
                        it.copy(
                            isLoading = false,
                            news = news,
                            attractions = attractions
                        )
                    }
                } else {
                    // 如果請求失敗，組合錯誤訊息
                    val errorMessage = attractionsResponse.errorBody()?.string() ?: newsResponse.errorBody()?.string() ?: "Unknown API error"
                    _state.update { it.copy(isLoading = false, error = "資料載入失敗: $errorMessage") }
                }
            } catch (e: Exception) {
                // 處理網路異常等例外情況
                _state.update { it.copy(isLoading = false, error = "資料載入失敗: ${e.message}") }
            }
        }
    }
}

