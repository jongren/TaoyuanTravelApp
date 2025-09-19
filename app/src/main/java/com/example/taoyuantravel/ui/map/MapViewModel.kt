package com.example.taoyuantravel.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
        
        // 桃園知名景點手動坐標對照表
        private val MANUAL_COORDINATES = mapOf(
            "大溪老街" to Pair(24.8838, 121.2888),
            "慈湖" to Pair(24.8642, 121.2736),
            "石門水庫" to Pair(24.8167, 121.2500),
            "拉拉山" to Pair(24.7167, 121.4167),
            "角板山" to Pair(24.8167, 121.3500),
            "小烏來瀑布" to Pair(24.8167, 121.3667),
            "桃園國際機場" to Pair(25.0797, 121.2342),
            "中壢夜市" to Pair(24.9536, 121.2252),
            "青埔高鐵站" to Pair(24.9136, 121.2161),
            "華泰名品城" to Pair(24.9136, 121.2161),
            "桃園忠烈祠" to Pair(24.9936, 121.3010),
            "虎頭山公園" to Pair(24.9936, 121.3010),
            "大園花海" to Pair(25.0597, 121.2042),
            "觀音蓮花園" to Pair(25.0297, 121.1342),
            "永安漁港" to Pair(25.0197, 121.2042),
            "竹圍漁港" to Pair(25.1397, 121.4042),
            "龍潭大池" to Pair(24.8636, 121.2152),
            "三坑老街" to Pair(24.8336, 121.2452),
            "大溪花海農場" to Pair(24.8738, 121.2988),
            "小人國主題樂園" to Pair(24.8336, 121.2152)
        )
    }

    private val _state = MutableStateFlow(MapUiState())
    val state: StateFlow<MapUiState> = _state.asStateFlow()

    init {
        loadAttractions()
    }

    /**
     * 載入景點資料
     */
    private fun loadAttractions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val response = travelRepository.getAttractions("zh-tw", 1)
                if (!response.isSuccessful) {
                    throw Exception("無法獲取景點資料")
                }
                
                val apiResponse = response.body() ?: throw Exception("回應資料為空")
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
            if (attraction.latitude == null || attraction.longitude == null) {
                // 首先檢查手動坐標對照表
                val manualCoordinates = findManualCoordinates(attraction.name)
                if (manualCoordinates != null) {
                    attraction.copy(
                        latitude = manualCoordinates.first,
                        longitude = manualCoordinates.second,
                        category = categorizeAttraction(attraction)
                    )
                } else {
                    // 如果手動坐標表中沒有，則嘗試地理編碼
                    try {
                        val address = extractAddress(attraction)
                        if (address.isNotEmpty()) {
                            val response = geocodingRepository.geocodeAddress(address)
                            if (response.isSuccessful && response.body()?.status == "OK") {
                                val location = response.body()?.results?.firstOrNull()?.geometry?.location
                                location?.let {
                                    attraction.copy(
                                        latitude = it.lat,
                                        longitude = it.lng,
                                        category = categorizeAttraction(attraction)
                                    )
                                } ?: run {
                                    attraction.copy(category = categorizeAttraction(attraction))
                                }
                            } else {
                                attraction.copy(category = categorizeAttraction(attraction))
                            }
                        } else {
                            attraction.copy(category = categorizeAttraction(attraction))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "景點 ${attraction.name} 地理編碼異常", e)
                        attraction.copy(category = categorizeAttraction(attraction))
                    }
                }
            } else {
                attraction.copy(category = categorizeAttraction(attraction))
            }
        }
    }

    /**
     * 從手動坐標對照表中查找景點坐標
     */
    private fun findManualCoordinates(attractionName: String): Pair<Double, Double>? {
        // 直接匹配
        MANUAL_COORDINATES[attractionName]?.let { return it }
        
        // 模糊匹配 - 檢查景點名稱是否包含對照表中的關鍵字
        for ((key, coordinates) in MANUAL_COORDINATES) {
            if (attractionName.contains(key) || key.contains(attractionName)) {
                return coordinates
            }
        }
        
        return null
    }

    /**
     * 從景點資料中提取地址
     */
    private fun extractAddress(attraction: Attraction): String {
        // 嘗試從介紹中提取地址資訊
        val introduction = attraction.introduction
        val addressKeywords = listOf("地址", "位於", "桃園市", "桃園縣")
        
        for (keyword in addressKeywords) {
            val index = introduction.indexOf(keyword)
            if (index != -1) {
                // 提取包含關鍵字後的一段文字作為地址
                val addressStart = if (keyword == "地址") index + keyword.length else index
                val addressEnd = minOf(
                    introduction.indexOf("。", addressStart).takeIf { it != -1 } ?: introduction.length,
                    introduction.indexOf("\n", addressStart).takeIf { it != -1 } ?: introduction.length,
                    addressStart + 100
                )
                val address = introduction.substring(addressStart, addressEnd).trim()
                if (address.length > 5) { // 確保地址有意義
                    return "桃園市$address"
                }
            }
        }
        
        // 如果沒有找到具體地址，使用景點名稱 + 桃園市
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
    fun selectAttraction(attraction: Attraction) {
        _state.update { it.copy(selectedAttraction = attraction) }
    }

    /**
     * 移動到使用者位置
     */
    fun moveToUserLocation() {
        // 這裡可以實現獲取使用者位置的邏輯
        // 暫時先不實現，因為需要位置權限處理
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