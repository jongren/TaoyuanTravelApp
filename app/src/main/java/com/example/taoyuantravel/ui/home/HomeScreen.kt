package com.example.taoyuantravel.ui.home

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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("桃園景點+") },
                actions = {
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = "切換語系")
                        }
                        LanguageDropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false },
                            onLanguageSelected = { lang ->
                                viewModel.onEvent(HomeEvent.ChangeLanguage(lang))
                                showLanguageMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        NewsSection(newsList = state.news)
                    }
                    item {
                        Text("熱門景點", style = MaterialTheme.typography.titleLarge)
                    }
                    items(state.attractions) { attraction ->
                        AttractionItem(attraction = attraction, modifier = Modifier.padding(bottom = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsSection(newsList: List<News>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("最新消息", style = MaterialTheme.typography.titleLarge)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(newsList) { news ->
                NewsItem(news = news)
            }
        }
    }
}

@Composable
private fun NewsItem(news: News) {
    ElevatedCard(
        modifier = Modifier
            .width(250.dp)
            .height(120.dp)
            .clickable { /* TODO: 開啟WebView */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = news.posted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AttractionItem(
    attraction: Attraction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* TODO: 進入詳情頁 */ }
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(attraction.images.firstOrNull()?.src)
                    .crossfade(true)
                    .build(),
                contentDescription = attraction.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop,
                // 圖片載入失敗時的預設圖
                onError = {
                    // 可以放一個本地的 placeholder 圖片
                }
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = attraction.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
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

@Composable
private fun LanguageDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = mapOf(
        "繁體中文" to "zh-tw",
        "English" to "en",
        "日本語" to "ja",
        "한국어" to "ko",
        "Español" to "es",
        "ภาษาไทย" to "th",
        "Tiếng Việt" to "vi"
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        languages.forEach { (name, code) ->
            DropdownMenuItem(
                text = { Text(name) },
                onClick = { onLanguageSelected(code) }
            )
        }
    }
}
