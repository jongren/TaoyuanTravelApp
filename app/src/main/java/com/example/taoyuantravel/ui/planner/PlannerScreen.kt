package com.example.taoyuantravel.ui.planner

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.ItineraryResponse
import com.example.taoyuantravel.data.model.ItineraryStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userInput by viewModel.userInput.collectAsState()
    val focusManager = LocalFocusManager.current

    // 當有新的行程結果生成時，自動收起鍵盤。
    LaunchedEffect(uiState.result) {
        if (uiState.result != null) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ai_planner_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        // **核心結構變更：使用 Box 來實現圖層疊加效果**
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var inputSectionHeight by remember { mutableStateOf(0) }
            val density = LocalDensity.current
            val inputSectionHeightDp = with(density) { inputSectionHeight.toDp() }

            val listState = rememberLazyListState()

            // **核心動畫邏輯：根據滾動偏移量計算 Y 軸位移**
            val translationY by remember {
                derivedStateOf {
                    // 只有當第一個列表項可見時，才進行位移計算
                    if (listState.firstVisibleItemIndex == 0) {
                        // 將滾動偏移量轉換為負的 Y 軸位移
                        -listState.firstVisibleItemScrollOffset.toFloat()
                    } else {
                        // 當第一個列表項完全滾出畫面後，將輸入區塊完全隱藏
                        -inputSectionHeight.toFloat()
                    }
                }
            }

            // 結果顯示區塊作為背景層，它負責滾動
            ResultDisplaySection(
                uiState = uiState,
                listState = listState,
                topPadding = inputSectionHeightDp // 傳入頂部間距，為輸入區塊留出空間
            )

            // 輸入區塊作為前景層，它會根據背景的滾動而移動
            InputSection(
                userInput = userInput,
                onInputChange = { viewModel.updateUserInput(it) },
                isLoading = uiState.isLoading,
                onGenerateClick = { viewModel.generateItinerary() },
                modifier = Modifier
                    .onSizeChanged { inputSectionHeight = it.height }
                    .graphicsLayer {
                        // 應用計算出的 Y 軸位移，並確保它不會超出範圍
                        this.translationY = translationY.coerceIn(-inputSectionHeight.toFloat(), 0f)
                    }
            )
        }
    }
}

@Composable
private fun InputSection(
    userInput: String,
    onInputChange: (String) -> Unit,
    isLoading: Boolean,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface) // 增加背景色，避免列表內容透出
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = userInput,
            onValueChange = onInputChange,
            label = { Text(stringResource(R.string.input_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )
        Text(
            text = stringResource(R.string.input_example),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
        Button(
            onClick = onGenerateClick,
            enabled = userInput.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (isLoading) stringResource(R.string.generate_button_loading) else stringResource(R.string.generate_button_idle),
                fontSize = 16.sp
            )
        }
    }
}


@Composable
private fun ResultDisplaySection(
    uiState: PlannerState,
    listState: LazyListState,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.error != null -> ErrorContent(uiState.error)
            uiState.result != null -> ItineraryResultContent(
                result = uiState.result,
                listState = listState,
                topPadding = topPadding
            )
            else -> EmptyContent()
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.ai_planning_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(error: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.empty_state_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ItineraryResultContent(
    result: ItineraryResponse,
    listState: LazyListState,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // **這個 Spacer 是關鍵，它在列表頂部創建了一個與輸入區塊等高的空間**
        item {
            Spacer(modifier = Modifier.height(topPadding))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (result.summary.isNotBlank()) {
                        Text(
                            text = result.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        itemsIndexed(result.steps) { index, step ->
            ItineraryStepCard(step = step, stepNumber = index + 1)
        }
    }
}

@Composable
private fun ItineraryStepCard(
    step: ItineraryStep,
    stepNumber: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$stepNumber. ${step.activity}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = stringResource(R.string.time_icon_description),
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = step.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = stringResource(R.string.location_icon_description),
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = step.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (step.description.isNotBlank()) {
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

