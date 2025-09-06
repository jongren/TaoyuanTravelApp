package com.example.taoyuantravel.ui.home

import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.ui.model.Language
import com.example.taoyuantravel.ui.navigation.Screen
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("桃園景點+") },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Language, contentDescription = "切換語系")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            state.languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName) },
                                    onClick = {
                                        viewModel.onEvent(HomeEvent.ChangeLanguage(lang.code))
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
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
            val currentError = state.error
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (currentError != null) {
                Text(
                    text = "資料載入失敗: $currentError",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // 最新消息區塊 (水平滑動)
                    stickyHeader {
                        ListHeader(title = "最新消息")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.news) { news ->
                                NewsItemHorizontalWithImage( // 使用新的含圖片的卡片
                                    news = news,
                                    onClick = {
                                        // 優先使用 Links 裡的網址，如果沒有再用 TYWebsite
                                        val url = news.links?.items?.firstOrNull()?.src?.trim()
                                            ?: "https://${news.url.trim()}"

                                        if (url.startsWith("http")) {
                                            val encodedUrl = Base64.encodeToString(url.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE)
                                            navController.navigate(Screen.WebView.createRoute(encodedUrl))
                                        }
                                        // 如果 url 不合法，則不執行任何操作
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 熱門景點區塊 (垂直列表)
                    stickyHeader {
                        ListHeader(title = "熱門景點")
                    }
                    items(state.attractions) { attraction ->
                        AttractionItem(
                            attraction = attraction,
                            onClick = {
                                val json = Uri.encode(Gson().toJson(attraction))
                                navController.navigate(Screen.Detail.createRoute(json))
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ListHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItemHorizontalWithImage(news: News, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(240.dp) // 將卡片加寬以容納圖片
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 新增的圖片區塊
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(news.images?.items?.firstOrNull()?.src)
                    .crossfade(true)
                    .build(),
                contentDescription = news.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            // 下方的文字區塊
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = news.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2, // 稍微減少行數以保持版面平衡
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionItem(
    attraction: Attraction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(attraction.images?.items?.firstOrNull()?.src)
                    .crossfade(true)
                    .build(),
                contentDescription = attraction.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = attraction.introduction,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

