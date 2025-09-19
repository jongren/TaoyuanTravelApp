package com.example.taoyuantravel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.language.LanguageManager
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
 * @param languageManager 語系管理器，用於處理語系切換和持久化
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaoyuanTravelRepository,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        initializeLanguage()
    }

    /**
     * 初始化語系設定
     */
    private fun initializeLanguage() {
        viewModelScope.launch {
            languageManager.getCurrentLanguage().collect { language ->
                _state.update { it.copy(selectedLanguage = language) }
                loadData()
            }
        }
    }

    /**
     * 處理UI事件
     * 
     * @param event 要處理的事件
     */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeLanguage -> {
                viewModelScope.launch {
                    // 根據傳入的 langCode 找到對應的 Language 物件
                    val newLang = Language.fromCode(event.langCode)
                    // 只有在語言真的改變時才觸發更新
                    if (newLang != _state.value.selectedLanguage) {
                        // 使用 LanguageManager 進行語系切換和持久化
                        languageManager.setLanguage(newLang)
                        // 狀態會透過 initializeLanguage 中的 collect 自動更新
                    }
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

