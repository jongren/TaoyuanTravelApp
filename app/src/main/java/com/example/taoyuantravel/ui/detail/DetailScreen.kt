package com.example.taoyuantravel.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.data.model.Image

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailViewModel
) {
    val state by viewModel.state.collectAsState()
    val attraction = state.attraction

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(attraction?.name ?: "景點詳情") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (attraction == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 圖片輪播區塊
                item {
                    val images = attraction.images?.items ?: emptyList()
                    if (images.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { images.size })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                // 安全地取得圖片
                                images.getOrNull(page)?.let { image ->
                                    ImageItem(image = image)
                                }
                            }
                            // 輪播指示器
                            Row(
                                Modifier
                                    .height(20.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(color) // 修正：使用 color 變數
                                            .size(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 文字資訊區塊
                item {
                    Column(Modifier.padding(16.dp)) {
                        Text(attraction.name, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(16.dp))
                        Text(attraction.introduction, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(24.dp))
                        InfoRow(label = "地址", value = attraction.address)
                        Spacer(Modifier.height(8.dp))
                        InfoRow(label = "開放時間", value = attraction.openTime)
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageItem(image: Image) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image.src)
            .crossfade(true)
            .placeholder(android.R.drawable.ic_menu_gallery) // 使用通用 placeholder
            .error(android.R.drawable.ic_dialog_alert)      // 使用通用 error drawable
            .build(),
        contentDescription = image.subject,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "$label：",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

