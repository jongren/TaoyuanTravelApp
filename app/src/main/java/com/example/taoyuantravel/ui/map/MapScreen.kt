package com.example.taoyuantravel.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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


    // 桃園市中心座標
    val taoyuanCenter = LatLng(24.9936, 121.3010)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(taoyuanCenter, 10f) // 降低縮放級別以顯示更大範圍
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
/*                    IconButton(onClick = { viewModel.moveToUserLocation() }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "我的位置")
                    }*/
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                    compassEnabled = true,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true
                ),
/*                onMapLoaded = {
                    // 地圖載入完成
                },
                onMapClick = { latLng ->
                    // 地圖被點擊
                }*/
            ) {
                // 景點標記
                state.filteredAttractions.forEach { attraction ->
                    if (attraction.latitude != null && attraction.longitude != null) {
                        val position = LatLng(attraction.latitude, attraction.longitude)
                        
                        Marker(
                            state = MarkerState(position = position),
                            title = attraction.name,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED), // 使用紅色標記
                            onClick = { marker ->
                                viewModel.selectAttraction(attraction)
                                true // 返回 true 以不顯示預設的資訊視窗
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
/*            Card(
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
            }*/

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

            // 景點詳細資訊面板
            state.selectedAttraction?.let { attraction ->
                AttractionDetailPanel(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    attraction = attraction,
                    onDismiss = { viewModel.selectAttraction(null) }
                )
            }

        }
    }
}

/**
 * 景點詳細資訊面板
 */
@Composable
private fun AttractionDetailPanel(
    modifier: Modifier = Modifier,
    attraction: Attraction,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 標題列
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "關閉",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 圖片顯示
            attraction.images?.items?.firstOrNull()?.let { image ->
                if (image.src.isNotEmpty()) {
                    AsyncImage(
                        model = image.src,
                        contentDescription = image.subject.ifEmpty { attraction.name },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 地址資訊
            if (attraction.address.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "地址：",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = attraction.address,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 開放時間
            if (attraction.openTime.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "開放時間：",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = attraction.openTime,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 景點簡介
            if (attraction.introduction.isNotEmpty()) {
                Text(
                    text = "簡介：",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = attraction.introduction,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
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