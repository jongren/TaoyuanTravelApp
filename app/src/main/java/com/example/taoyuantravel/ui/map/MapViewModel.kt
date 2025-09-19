package com.example.taoyuantravel.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taoyuantravel.data.language.LanguageManager
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.repository.GeocodingRepository
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 地圖畫面的 ViewModel
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val travelRepository: TaoyuanTravelRepository,
    private val geocodingRepository: GeocodingRepository,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    init {
        // 初始載入預設語系的資料
        loadAttractions("zh-tw")
    }

    /**
     * 載入景點資料
     */
    private fun loadAttractions() {
        loadAttractions("zh-tw") // 預設使用繁體中文
    }

    /**
     * 根據語言載入景點資料
     */
    fun loadAttractions(language: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val response = travelRepository.getAttractions(language, 1)
                
                if (!response.isSuccessful) {
                    throw Exception("無法獲取景點資料，狀態碼: ${response.code()}")
                }
                
                val apiResponse = response.body()
                if (apiResponse == null) {
                    throw Exception("回應資料為空")
                }
                
                val attractions = apiResponse.infos.data
                
                // 為沒有座標的景點進行地理編碼
                val attractionsWithCoordinates = geocodeAttractions(attractions)
                
                val categories = extractCategories(attractionsWithCoordinates)
                
                _state.update { currentState ->
                    currentState.copy(
                        attractions = attractionsWithCoordinates,
                        filteredAttractions = attractionsWithCoordinates,
                        availableCategories = categories,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "載入景點資料失敗: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * 為景點進行地理編碼
     */
    private suspend fun geocodeAttractions(attractions: List<Attraction>): List<Attraction> {
        return attractions.map { attraction ->
            var updatedAttraction = attraction.copy(category = categorizeAttraction(attraction))

            if (attraction.latitude == null || attraction.longitude == null) {
                try {
                    val address = extractAddress(attraction)
                    if (address.isNotEmpty()) {
                        val response = geocodingRepository.geocodeAddress(address)
                        if (response.isSuccessful && response.body()?.status == "OK") {
                            val result = response.body()?.results?.firstOrNull()
                            val location = result?.geometry?.location
                            if (location != null) {
                                updatedAttraction = attraction.copy(
                                    latitude = location.lat,
                                    longitude = location.lng,
                                    category = categorizeAttraction(attraction)
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // 地理編碼失敗時保持原始景點資料
                }
            } else {
                updatedAttraction = attraction.copy(category = categorizeAttraction(attraction))
            }
            updatedAttraction
        }
    }

    /**
     * 從景點資料中提取地址
     */
    private fun extractAddress(attraction: Attraction): String {
        val introduction = attraction.introduction
        val addressKeywords = listOf("地址", "位於", "桃園市", "桃園縣")
        
        for (keyword in addressKeywords) {
            val index = introduction.indexOf(keyword)
            if (index != -1) {
                val addressStart = if (keyword == "地址") index + keyword.length else index
                val addressEnd = minOf(
                    introduction.indexOf("。", addressStart).takeIf { it != -1 } ?: introduction.length,
                    introduction.indexOf("\n", addressStart).takeIf { it != -1 } ?: introduction.length,
                    addressStart + 100
                )
                val address = introduction.substring(addressStart, addressEnd).trim()
                if (address.length > 5) {
                    return "桃園市$address"
                }
            }
        }
        
        return "桃園市 ${attraction.name}"
    }

    /**
     * 根據景點名稱和介紹進行分類
     */
    private fun categorizeAttraction(attraction: Attraction): String {
        val name = attraction.name.lowercase()
        val intro = attraction.introduction.lowercase()
        
        return when {
            name.contains("公園") || intro.contains("公園") -> "公園綠地"
            name.contains("廟") || name.contains("宮") || intro.contains("廟") || intro.contains("宮") -> "宗教文化"
            name.contains("博物館") || name.contains("館") || intro.contains("博物館") -> "博物館"
            name.contains("夜市") || name.contains("市場") || intro.contains("夜市") -> "美食購物"
            name.contains("步道") || name.contains("山") || intro.contains("步道") || intro.contains("登山") -> "自然步道"
            name.contains("老街") || intro.contains("老街") || intro.contains("古蹟") -> "歷史古蹟"
            name.contains("觀光工廠") || intro.contains("觀光工廠") -> "觀光工廠"
            name.contains("農場") || name.contains("休閒農業") || intro.contains("農場") -> "休閒農業"
            else -> "其他景點"
        }
    }

    /**
     * 提取所有可用的分類
     */
    private fun extractCategories(attractions: List<Attraction>): List<String> {
        return attractions.mapNotNull { it.category }.distinct().sorted()
    }

    /**
     * 切換篩選器顯示狀態
     */
    fun toggleFilter() {
        _state.update { it.copy(showFilter = !it.showFilter) }
    }

    /**
     * 切換分類篩選
     */
    fun toggleCategory(category: String) {
        _state.update { currentState ->
            val newSelectedCategories = if (category in currentState.selectedCategories) {
                currentState.selectedCategories - category
            } else {
                currentState.selectedCategories + category
            }
            
            val filteredAttractions = if (newSelectedCategories.isEmpty()) {
                currentState.attractions
            } else {
                currentState.attractions.filter { attraction ->
                    attraction.category in newSelectedCategories
                }
            }
            
            currentState.copy(
                selectedCategories = newSelectedCategories,
                filteredAttractions = filteredAttractions
            )
        }
    }

    /**
     * 選擇景點
     */
    fun selectAttraction(attraction: Attraction?) {
        _state.update { it.copy(selectedAttraction = attraction) }
    }

    /**
     * 移動到使用者位置
     */
    fun moveToUserLocation() {
        // 這裡可以實現獲取使用者位置的邏輯
    }

    /**
     * 設置位置權限狀態
     */
    fun setLocationPermissionGranted(granted: Boolean) {
        _state.update { it.copy(isLocationPermissionGranted = granted) }
    }
}

/**
 * 地圖 UI 狀態
 */
data class MapUiState(
    val attractions: List<Attraction> = emptyList(),
    val filteredAttractions: List<Attraction> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedAttraction: Attraction? = null,
    val showFilter: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLocationPermissionGranted: Boolean = false
)