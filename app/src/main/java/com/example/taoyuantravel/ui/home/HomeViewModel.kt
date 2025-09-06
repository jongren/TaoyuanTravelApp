package com.example.taoyuantravel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaoyuanTravelRepository
) : ViewModel() {

    // 私有的、可變的 StateFlow，僅供 ViewModel 內部使用
    private val _state = MutableStateFlow(HomeState())
    // 公開的、唯讀的 StateFlow，供 UI 訂閱狀態變化
    val state = _state.asStateFlow()

    init {
        // ViewModel 初始化時，觸發載入資料的事件
        onEvent(HomeEvent.LoadData)
    }

    /**
     * 處理所有從 UI 傳來的事件
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadData -> {
                fetchData(state.value.selectedLanguage)
            }
            is HomeEvent.ChangeLanguage -> {
                // 當語言改變時，更新狀態並重新擷取資料
                _state.update { it.copy(selectedLanguage = event.lang) }
                fetchData(event.lang)
            }
        }
    }

    /**
     * 根據指定語言，從 Repository 取得最新消息與景點資料
     * @param lang 語系代碼
     */
    private fun fetchData(lang: String) {
        viewModelScope.launch {
            // 1. 開始載入，更新 UI 狀態為 loading
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // 2. 使用 async 並行處理兩個 API 請求，提升效率
                val newsDeferred = async { repository.getNews(lang, 1) }
                val attractionsDeferred = async { repository.getAttractions(lang, 1) }

                val newsResponse = newsDeferred.await()
                val attractionsResponse = attractionsDeferred.await()

                // 3. 成功取得資料，更新 UI 狀態
                _state.update {
                    it.copy(
                        isLoading = false,
                        news = newsResponse.data,
                        attractions = attractionsResponse.data
                    )
                }
            } catch (e: Exception) {
                // 4. 發生錯誤，更新 UI 狀態並顯示錯誤訊息
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "資料載入失敗: ${e.message}"
                    )
                }
            }
        }
    }
}
