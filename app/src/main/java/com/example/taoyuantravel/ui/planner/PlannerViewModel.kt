package com.example.taoyuantravel.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 行程規劃器ViewModel，管理行程規劃的UI狀態和業務邏輯
 * 
 * 目前使用模擬數據，後續會整合真實的AI服務
 */
@HiltViewModel
class PlannerViewModel @Inject constructor(
    // 後續會注入AI服務或Repository
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
            is PlannerEvent.UpdateUserInput -> {
                _state.update { it.copy(userInput = event.input) }
            }
            
            PlannerEvent.GenerateItinerary -> {
                generateItinerary()
            }
            
            PlannerEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
            
            PlannerEvent.Reset -> {
                _state.update { PlannerState() }
            }
        }
    }

    /**
     * 生成行程（目前使用模擬數據）
     * 後續會替換為真實的AI API調用
     */
    private fun generateItinerary() {
        val userInput = _state.value.userInput.trim()
        
        if (userInput.isBlank()) {
            _state.update { it.copy(error = "請輸入您的旅遊偏好") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                // 模擬API調用延遲
                delay(2000)
                
                // 根據使用者輸入生成不同的模擬行程
                val mockItinerary = generateMockItinerary(userInput)
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        itinerary = mockItinerary,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "生成行程時發生錯誤"
                    )
                }
            }
        }
    }

    /**
     * 根據使用者輸入生成模擬行程
     * 這是臨時的模擬邏輯，後續會替換為AI服務
     */
    private fun generateMockItinerary(userInput: String): List<ItineraryItem> {
        val input = userInput.lowercase()
        
        return when {
            input.contains("半日") && input.contains("風景") && input.contains("咖啡") -> {
                listOf(
                    ItineraryItem(
                        time = "09:00",
                        activity = "大溪老街漫步",
                        location = "大溪老街",
                        description = "體驗傳統建築與在地小吃，感受歷史文化氛圍"
                    ),
                    ItineraryItem(
                        time = "11:00",
                        activity = "老街咖啡時光",
                        location = "大溪老街咖啡館",
                        description = "品嚐在地特色咖啡，欣賞老街風情"
                    ),
                    ItineraryItem(
                        time = "13:00",
                        activity = "慈湖風景區",
                        location = "慈湖",
                        description = "欣賞湖光山色，感受自然之美與寧靜氛圍"
                    )
                )
            }
            
            input.contains("一日") || input.contains("全日") -> {
                listOf(
                    ItineraryItem(
                        time = "08:30",
                        activity = "石門水庫",
                        location = "石門水庫風景區",
                        description = "欣賞水庫美景，體驗大自然的壯麗"
                    ),
                    ItineraryItem(
                        time = "10:30",
                        activity = "大溪老街",
                        location = "大溪老街",
                        description = "探索歷史建築，品嚐傳統美食"
                    ),
                    ItineraryItem(
                        time = "12:30",
                        activity = "午餐時光",
                        location = "大溪在地餐廳",
                        description = "享用道地桃園美食"
                    ),
                    ItineraryItem(
                        time = "14:00",
                        activity = "慈湖陵寢",
                        location = "慈湖",
                        description = "參觀歷史景點，了解台灣近代史"
                    ),
                    ItineraryItem(
                        time = "16:00",
                        activity = "角板山公園",
                        location = "角板山",
                        description = "登高望遠，欣賞山林美景"
                    )
                )
            }
            
            input.contains("親子") || input.contains("小孩") -> {
                listOf(
                    ItineraryItem(
                        time = "09:00",
                        activity = "桃園市兒童美術館",
                        location = "桃園市兒童美術館",
                        description = "適合親子同遊的藝術空間，激發孩子創意"
                    ),
                    ItineraryItem(
                        time = "11:00",
                        activity = "虎頭山公園",
                        location = "虎頭山公園",
                        description = "寬敞的公園空間，讓孩子盡情玩耍"
                    ),
                    ItineraryItem(
                        time = "13:30",
                        activity = "桃園觀光夜市",
                        location = "桃園觀光夜市",
                        description = "品嚐各式小吃，體驗台灣夜市文化"
                    )
                )
            }
            
            else -> {
                // 預設行程
                listOf(
                    ItineraryItem(
                        time = "09:00",
                        activity = "桃園市區探索",
                        location = "桃園車站周邊",
                        description = "從桃園車站開始，探索市區景點"
                    ),
                    ItineraryItem(
                        time = "11:00",
                        activity = "在地美食體驗",
                        location = "桃園在地餐廳",
                        description = "品嚐桃園特色料理"
                    ),
                    ItineraryItem(
                        time = "14:00",
                        activity = "文化景點參訪",
                        location = "桃園文化景點",
                        description = "了解桃園的歷史文化"
                    )
                )
            }
        }
    }
}