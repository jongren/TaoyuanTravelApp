package com.example.taoyuantravel.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.ui.navigation.Screen
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController, // <--- 修正點：新增 NavController 參數
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
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        NewsSection(newsList = state.news)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            "熱門景點",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(state.attractions) { attraction ->
                        AttractionItem(
                            attraction = attraction,
                            onItemClick = {
                                val attractionJson = Gson().toJson(it)
                                navController.navigate(Screen.Detail.createRoute(attractionJson))
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsSection(newsList: List<News>) {
    Column {
        Text(
            "最新消息",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(newsList) { news ->
                NewsItem(news = news, onItemClick = { /* TODO: Handle WebView */ })
            }
        }
    }
}

@Composable
private fun NewsItem(news: News, onItemClick: (News) -> Unit) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .clickable { onItemClick(news) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = news.title,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AttractionItem(
    attraction: Attraction,
    onItemClick: (Attraction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(attraction) }
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(attraction.images.firstOrNull()?.src)
                    .crossfade(true)
                    .build(),
                contentDescription = attraction.name,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(attraction.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    attraction.introduction,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LanguageDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = mapOf(
        "zh-tw" to "正體中文",
        "en" to "English",
        "ja" to "日本語",
        "ko" to "한국어",
        "es" to "Español",
        "id" to "Indonesia",
        "th" to "ภาษาไทย",
        "vi" to "Tiếng Việt"
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        languages.forEach { (code, name) ->
            DropdownMenuItem(
                text = { Text(name) },
                onClick = { onLanguageSelected(code) }
            )
        }
    }
}

