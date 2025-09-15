package com.example.taoyuantravel.ui.planner

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.repository.GeminiRepository
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.example.taoyuantravel.ui.home.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class PlannerViewModel constructor(
    private val geminiRepository: GeminiRepository,
    private val taoyuanTravelRepository: TaoyuanTravelRepository,
    private val homeViewModel: HomeViewModel,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerState())
    val uiState: StateFlow<PlannerState> = _uiState.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    /**
     * 更新用戶輸入
     */
    fun updateUserInput(input: String) {
        _userInput.update { input }
    }

    /**
     * 生成 AI 行程
     */
    fun generateItinerary() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val attractionsResponse = taoyuanTravelRepository.getAttractions(
                    lang = homeViewModel.state.value.selectedLanguage.code,
                    page = 1
                )
                
                if (!attractionsResponse.isSuccessful) {
                    throw Exception(context.getString(R.string.error_no_attractions_data))
                }
                
                val attractionsData = attractionsResponse.body()
                    ?: throw Exception(context.getString(R.string.error_invalid_response_format))
                
                val attractionsList = attractionsData.infos.data
                if (attractionsList.isEmpty()) {
                    throw Exception(context.getString(R.string.error_no_attractions_found))
                }
                
                val attractionsJson = Gson().toJson(attractionsList)
                
                val result = geminiRepository.generateItinerary(
                    userPrompt = _userInput.value,
                    attractionsJson = attractionsJson
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        result = result,
                        error = null
                    )
                }
                
            } catch (e: Exception) {
                android.util.Log.e("PlannerViewModel", "生成行程時發生錯誤", e)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: context.getString(R.string.error_unknown)
                    )
                }
            }
        }
    }
}