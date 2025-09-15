package com.example.taoyuantravel.ui.detail

import android.util.Base64
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.Image
import com.example.taoyuantravel.ui.navigation.Screen
import java.nio.charset.StandardCharsets

/**
 * 景點詳情畫面，顯示景點的詳細資訊和圖片
 * 
 * @param navController 導航控制器，用於頁面跳轉
 * @param viewModel 詳情頁的ViewModel，管理景點資料
 */
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
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                val errorMessage = state.error ?: "未知錯誤"
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigateUp() }
                        ) {
                            Text("返回")
                        }
                    }
                }
            }
            attraction != null -> {
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
                        
                        // 調整順序：開放時間 > 地址 > 電話 > 官方網站
                        InfoRow(label = stringResource(id = R.string.open_time), value = attraction.openTime)
                        Spacer(Modifier.height(8.dp))
                        InfoRow(label = stringResource(id = R.string.address), value = attraction.address)
                        
                        // 檢查是否有電話號碼（從Links中尋找）
                        val phoneLink = attraction.links?.items?.find { it.subject.contains("電話", ignoreCase = true) || 
                                                                       it.subject.contains("Tel", ignoreCase = true) || 
                                                                       it.subject.contains("Phone", ignoreCase = true) }
                        if (phoneLink != null) {
                            Spacer(Modifier.height(8.dp))
                            InfoRow(label = "電話", value = phoneLink.src)
                        }
                        
                        // 檢查是否有官方網站（從Links中尋找）
                        val websiteLink = attraction.links?.items?.find { it.subject.contains("網站", ignoreCase = true) || 
                                                                        it.subject.contains("網址", ignoreCase = true) || 
                                                                        it.subject.contains("官網", ignoreCase = true) || 
                                                                        it.subject.contains("Website", ignoreCase = true) || 
                                                                        it.subject.contains("Site", ignoreCase = true) }
                        if (websiteLink != null) {
                            Spacer(Modifier.height(8.dp))
                            val navController = navController
                            val context = LocalContext.current
                            
                            // 官方網站可點擊
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 使用完整URL進行Base64編碼，並導航到WebView頁面，同時傳遞景點名稱作為標題
                                        val fullUrl = websiteLink.src.trim()
                                        // 確保URL包含http或https前綴
                                        val processedUrl = if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
                                            "https://$fullUrl"
                                        } else {
                                            fullUrl
                                        }

                                        val encodedUrl = Base64.encodeToString(processedUrl.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE)
                                        navController.navigate(Screen.WebView.createRoute(encodedUrl, attraction.name))
                                    },
                                verticalAlignment = Alignment.Top
                            ) {
                                // 只顯示域名部分
                                val domain = try {
                                    val url = websiteLink.src
                                    val regex = "(?:https?://)?(?:www\\.)?([^/]+)".toRegex()
                                    val matchResult = regex.find(url)
                                    matchResult?.groupValues?.get(1) ?: url
                                } catch (e: Exception) {
                                    websiteLink.src
                                }
                                
                                Text(
                                    "${stringResource(id = R.string.official_website)}：$domain",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        // 將介紹(Description)移到最後
                        Spacer(Modifier.height(24.dp))
                        Text(attraction.introduction, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            }
        }
    }
}

/**
 * 圖片項目組件，顯示單張景點圖片
 * 
 * @param image 圖片資料
 */
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

/**
 * 資訊行組件，顯示標籤和對應的值
 * 
 * @param label 標籤文字
 * @param value 對應的值
 */
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

