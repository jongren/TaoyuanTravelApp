package com.example.taoyuantravel.ui.home

import android.net.Uri
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.ui.navigation.Screen
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.nio.charset.StandardCharsets

/**
 * 首頁畫面，顯示最新消息和熱門景點列表
 * 
 * @param navController 導航控制器，用於頁面跳轉
 * @param viewModel 首頁的ViewModel，管理UI狀態和業務邏輯
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    
    // 進入動畫狀態
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    // TopBar 動畫
    val topBarAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "topBarAlpha"
    )
    
    val topBarTranslationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -50f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "topBarTranslation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier.graphicsLayer {
                            alpha = topBarAlpha
                            translationY = topBarTranslationY
                        }
                    ) 
                },
                actions = {
                    Box(
                        modifier = Modifier.graphicsLayer {
                            alpha = topBarAlpha
                            translationY = topBarTranslationY
                        }
                    ) {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.scale(
                                animateFloatAsState(
                                    targetValue = if (menuExpanded) 1.1f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "iconScale"
                                ).value
                            )
                        ) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val currentError = state.error
            
            // 內容動畫
            val contentAlpha by animateFloatAsState(
                targetValue = if (isVisible && !state.isLoading) 1f else 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 300, easing = FastOutSlowInEasing),
                label = "contentAlpha"
            )
            
            val contentTranslationY by animateFloatAsState(
                targetValue = if (isVisible && !state.isLoading) 0f else 30f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 300, easing = FastOutSlowInEasing),
                label = "contentTranslation"
            )
            
            if (state.isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "載入中...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (currentError != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "資料載入失敗: $currentError",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = contentAlpha
                            translationY = contentTranslationY
                        },
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // 最新消息區塊 (水平滑動)
                    stickyHeader {
                        ListHeader(title = stringResource(id = R.string.latest_news))
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(state.news) { index, news ->
                                val latestNewsTitle = stringResource(id = R.string.latest_news)
                                
                                // 每個新聞項目的進入動畫
                                var itemVisible by remember { mutableStateOf(false) }
                                
                                LaunchedEffect(isVisible) {
                                    if (isVisible) {
                                        delay((index * 100).toLong())
                                        itemVisible = true
                                    }
                                }
                                
                                val itemScale by animateFloatAsState(
                                    targetValue = if (itemVisible) 1f else 0.8f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "newsItemScale"
                                )
                                
                                val itemAlpha by animateFloatAsState(
                                    targetValue = if (itemVisible) 1f else 0f,
                                    animationSpec = tween(durationMillis = 600),
                                    label = "newsItemAlpha"
                                )
                                
                                NewsItemHorizontalWithImage(
                                    news = news,
                                    modifier = Modifier
                                        .scale(itemScale)
                                        .graphicsLayer { alpha = itemAlpha },
                                    onClick = {
                                        val url = news.links?.items?.firstOrNull()?.src?.trim()
                                            ?: "https://${news.url.trim()}"

                                        // 確保URL包含http或https前綴
                                        val processedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                            "https://$url"
                                        } else {
                                            url
                                        }
                                        
                                        val encodedUrl = Base64.encodeToString(processedUrl.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE)
                                        navController.navigate(Screen.WebView.createRoute(encodedUrl, latestNewsTitle))
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 熱門景點區塊 (垂直列表)
                    stickyHeader {
                        ListHeader(title = stringResource(id = R.string.popular_attractions))
                    }
                    itemsIndexed(state.attractions) { index, attraction ->
                        // 每個景點項目的進入動畫
                        var attractionVisible by remember { mutableStateOf(false) }
                        
                        LaunchedEffect(isVisible) {
                            if (isVisible) {
                                delay((index * 150 + 500).toLong()) // 延遲更長，讓新聞先出現
                                attractionVisible = true
                            }
                        }
                        
                        val attractionScale by animateFloatAsState(
                            targetValue = if (attractionVisible) 1f else 0.9f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "attractionScale"
                        )
                        
                        val attractionAlpha by animateFloatAsState(
                            targetValue = if (attractionVisible) 1f else 0f,
                            animationSpec = tween(durationMillis = 800),
                            label = "attractionAlpha"
                        )
                        
                        val attractionTranslationX by animateFloatAsState(
                            targetValue = if (attractionVisible) 0f else 50f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "attractionTranslationX"
                        )
                        
                        AttractionItem(
                            attraction = attraction,
                            onClick = {
                                val json = Uri.encode(Gson().toJson(attraction))
                                navController.navigate(Screen.Detail.createRoute(json))
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .scale(attractionScale)
                                .graphicsLayer {
                                    alpha = attractionAlpha
                                    translationX = attractionTranslationX
                                }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * 列表標題組件，用於顯示區塊標題
 * 
 * @param title 要顯示的標題文字
 */
@Composable
fun ListHeader(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 橫向新聞項目組件，包含圖片和文字內容
 * 
 * @param news 新聞資料
 * @param onClick 點擊事件回調
 * @param modifier 修飾符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItemHorizontalWithImage(
    news: News, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .width(240.dp)
            .scale(cardScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = !isPressed
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


/**
 * 景點項目組件，顯示景點資訊卡片
 * 
 * @param attraction 景點資料
 * @param onClick 點擊事件回調
 * @param modifier 修飾符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionItem(
    attraction: Attraction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 12.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardElevation"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(attraction.images?.items?.firstOrNull()?.src)
                        .crossfade(true)
                        .build(),
                    contentDescription = attraction.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                // 漸層遮罩，讓文字更清楚
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 100f
                            )
                        )
                )
            }
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = attraction.introduction,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

