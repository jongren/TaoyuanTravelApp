package com.example.taoyuantravel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.example.taoyuantravel.ui.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首頁ViewModel，管理首頁的UI狀態和業務邏輯
 * 
 * @param repository 桃園旅遊資料倉庫，用於獲取新聞和景點資料
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaoyuanTravelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    /**
     * 處理UI事件
     * 
     * @param event 要處理的事件
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeLanguage -> {
                // 根據傳入的 langCode 找到對應的 Language 物件
                val newLang = state.value.languages.find { it.code == event.langCode }
                // 只有在語言真的改變時才觸發更新和重載
                if (newLang != null && newLang != _state.value.selectedLanguage) {
                    _state.update { it.copy(selectedLanguage = newLang) }
                    loadData()
                }
            }
            HomeEvent.LoadData -> loadData()
        }
    }

    /**
     * 載入首頁資料（新聞和景點）
     */
    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            runCatching {
                val currentLangCode = _state.value.selectedLanguage.code

                val newsDeferred = async { repository.getNews(currentLangCode, 1) }
                val attractionsDeferred = async { repository.getAttractions(currentLangCode, 1) }

                val newsResponse = newsDeferred.await()
                val attractionsResponse = attractionsDeferred.await()

                val newsData = if (newsResponse.isSuccessful) {
                    newsResponse.body()?.infos?.data ?: emptyList()
                } else {
                    emptyList()
                }
                
                val attractionsData = if (attractionsResponse.isSuccessful) {
                    attractionsResponse.body()?.infos?.data ?: emptyList()
                } else {
                    emptyList()
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        news = newsData,
                        attractions = attractionsData,
                        error = null
                    )
                }
            }.onFailure { exception ->
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = exception.message ?: "未知錯誤"
                    ) 
                }
            }
        }
    }
}

