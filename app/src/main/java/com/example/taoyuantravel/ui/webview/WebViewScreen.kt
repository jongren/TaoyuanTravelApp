package com.example.taoyuantravel.ui.webview

import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    navController: NavController,
    encodedUrl: String?,
    title: String? = null // 添加標題參數，預設為null
) {
    // 將 Base64 編碼的 URL 解碼回原始 URL
    val url = remember(encodedUrl) {
        if (encodedUrl != null) {
            try {
                String(Base64.decode(encodedUrl, Base64.NO_WRAP))
            } catch (e: IllegalArgumentException) {
                null // 如果解碼失敗則返回 null
            }
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = title ?: "網頁內容",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                }, // 使用傳入的標題，如果為null則顯示「網頁內容」
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
            if (url != null) {
                // AndroidView 是 Jetpack Compose 中用來嵌入傳統 Android View 的方法
                AndroidView(
                    factory = { context ->
                        // 使用 factory 提供的上下文創建 WebView
                        WebView(context).apply {
                            // 進行 WebView 的基本設定
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient() // 確保連結在 WebView 內開啟
                            settings.javaScriptEnabled = true // 啟用 JavaScript
                            loadUrl(url)
                        }
                    },
                    // 當 url 改變時，更新 WebView 的內容
                    update = { webView ->
                        webView.loadUrl(url)
                    }
                )
            } else {
                // 如果 URL 無效，顯示錯誤訊息
                Text(
                    text = "無法載入頁面，網址無效。",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
