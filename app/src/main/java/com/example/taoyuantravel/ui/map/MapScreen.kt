package com.example.taoyuantravel.ui.map

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.Attraction
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.maps.android.compose.*

/**
 * 交互式景點地圖畫面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    // 檢查Google Play Services可用性
    LaunchedEffect(Unit) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        when (resultCode) {
            ConnectionResult.SUCCESS -> {
                Log.d("MapScreen", "Google Play Services可用")
            }
            else -> {
                Log.e("MapScreen", "Google Play Services不可用，錯誤代碼: $resultCode")
                Log.e("MapScreen", "錯誤描述: ${googleApiAvailability.getErrorString(resultCode)}")
            }
        }
        
        // 檢查API Key配置
        try {
            val packageInfo = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
            val apiKey = packageInfo.metaData?.getString("com.google.android.geo.API_KEY")
            Log.d("MapScreen", "Google Maps API Key 狀態: ${if (apiKey.isNullOrEmpty()) "未設置" else "已設置 (${apiKey.take(10)}...)"}")
        } catch (e: Exception) {
            Log.e("MapScreen", "無法檢查API Key: ${e.message}")
        }
    }

    // 桃園市中心座標
    val taoyuanCenter = LatLng(24.9936, 121.3010)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(taoyuanCenter, 10f) // 降低縮放級別以顯示更大範圍
    }
    
    // 當景點載入完成後，調整地圖視角以包含所有景點
    LaunchedEffect(state.filteredAttractions) {
        if (state.filteredAttractions.isNotEmpty()) {
            val attractionsWithCoords = state.filteredAttractions.filter { 
                it.latitude != null && it.longitude != null 
            }
            Log.d("MapScreen", "準備調整地圖視角，包含 ${attractionsWithCoords.size} 個景點")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("景點地圖") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFilter() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "篩選")
                    }
                    IconButton(onClick = { viewModel.moveToUserLocation() }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "我的位置")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Google Maps - 簡化配置用於測試
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = false,
                    isBuildingEnabled = true,
                    isIndoorEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true, // 啟用縮放控制以便測試
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true
                ),
                onMapLoaded = {
                    Log.d("MapScreen", "✅ Google地圖載入完成！")
                    Log.d("MapScreen", "✅ 地圖可見性: 地圖已渲染並可見")
                    Log.d("MapScreen", "✅ 地圖瓦片應該現在可以顯示")
                    Log.d("MapScreen", "✅ 當前縮放級別: ${cameraPositionState.position.zoom}")
                    Log.d("MapScreen", "✅ 當前中心點: ${cameraPositionState.position.target}")
                },
                onMapClick = { latLng ->
                    Log.d("MapScreen", "🎯 地圖被點擊: ${latLng.latitude}, ${latLng.longitude}")
                }
            ) {
                // 景點標記
                Log.d("MapScreen", "總景點數量: ${state.filteredAttractions.size}")
                Log.d("MapScreen", "有座標的景點數量: ${state.filteredAttractions.count { it.latitude != null && it.longitude != null }}")
                
                state.filteredAttractions.forEach { attraction ->
                    if (attraction.latitude != null && attraction.longitude != null) {
                        val position = LatLng(attraction.latitude, attraction.longitude)
                        Log.d("MapScreen", "顯示標記: ${attraction.name} at (${attraction.latitude}, ${attraction.longitude})")
                        
                        Marker(
                            state = MarkerState(position = position),
                            title = attraction.name,
                            snippet = attraction.introduction.take(100),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED), // 使用紅色標記
                            onClick = { marker ->
                                viewModel.selectAttraction(attraction)
                                false // 返回 false 以顯示預設的資訊視窗
                            }
                        )
                    }
                }
            }

            // 篩選器
            if (state.showFilter) {
                FilterPanel(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    categories = state.availableCategories,
                    selectedCategories = state.selectedCategories,
                    onCategoryToggle = viewModel::toggleCategory,
                    onDismiss = { viewModel.toggleFilter() }
                )
            }

            // 狀態指示器 - 用於調試
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "地圖狀態",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "景點總數: ${state.filteredAttractions.size}")
                    Text(text = "有坐標: ${state.filteredAttractions.count { it.latitude != null && it.longitude != null }}")
                    Text(text = "載入中: ${if (state.isLoading) "是" else "否"}")
                }
            }

            // 載入指示器
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("正在載入景點資料...")
                        }
                    }
                }
            }

            // 錯誤訊息
            state.errorMessage?.let { error ->
                LaunchedEffect(error) {
                    // 可以在這裡顯示 Snackbar 或其他錯誤提示
                }
            }
        }
    }
}

/**
 * 篩選面板
 */
@Composable
private fun FilterPanel(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategories: Set<String>,
    onCategoryToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "景點分類",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onDismiss) {
                    Text("完成")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = { onCategoryToggle(category) },
                        label = { Text(category) }
                    )
                }
            }
        }
    }
}