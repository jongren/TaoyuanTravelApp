package com.example.taoyuantravel.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.repository.GeminiRepository
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 行程規劃器ViewModel，管理行程規劃的UI狀態和業務邏輯
 * 
 * 使用 Hilt 注入並包含 MutableStateFlow 來管理 PlannerState
 */
@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val taoyuanTravelRepository: TaoyuanTravelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlannerState())
    val state: StateFlow<PlannerState> = _state.asStateFlow()

    /**
     * 處理UI事件
     * 
     * @param event 要處理的事件
     */
    fun onEvent(event: PlannerEvent) {
        when (event) {
            is PlannerEvent.OnUserInputChanged -> {
                _state.update { it.copy(userInput = event.text) }
            }
            
            PlannerEvent.GenerateItinerary -> {
                generateItinerary()
            }
        }
    }
    
    /**
     * 生成 AI 行程
     */
    private fun generateItinerary() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                
                val attractionsResponse = taoyuanTravelRepository.getAttractions(
                    lang = "zh-tw",
                    page = 1
                )
                
                if (!attractionsResponse.isSuccessful) {
                    throw Exception("無法獲取景點資料：${attractionsResponse.message()}")
                }
                
                val attractionsData = attractionsResponse.body()
                    ?: throw Exception("景點資料回應格式錯誤")
                
                val attractionsList = attractionsData.infos.data
                if (attractionsList.isEmpty()) {
                    throw Exception("沒有找到景點資料，請稍後再試")
                }
                
                val attractionsJson = Gson().toJson(attractionsList)
                
                val result = geminiRepository.generateItinerary(
                    userPrompt = _state.value.userInput,
                    attractionsJson = attractionsJson
                )
                
                _state.update { 
                    it.copy(
                        isLoading = false,
                        itineraryResult = result,
                        error = null
                    )
                }
                
            } catch (e: Exception) {
                android.util.Log.e("PlannerViewModel", "生成行程時發生錯誤", e)
                
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "發生未知錯誤"
                    )
                }
            }
        }
    }
}