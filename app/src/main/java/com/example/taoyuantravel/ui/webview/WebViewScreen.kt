package com.example.taoyuantravel.ui.webview

import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

/**
 * WebView畫面，用於顯示網頁內容
 * 
 * @param navController 導航控制器，用於頁面跳轉
 * @param encodedUrl Base64編碼的URL
 * @param title 頁面標題
 */
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
                val decodedUrl = String(Base64.decode(encodedUrl, Base64.URL_SAFE))
                Log.d("WebViewScreen", "解碼URL: $decodedUrl")
                decodedUrl
            } catch (e: IllegalArgumentException) {
                Log.e("WebViewScreen", "URL解碼失敗: $encodedUrl", e)
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
                            // 自定義 WebViewClient 來處理錯誤和記錄日誌
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    Log.d("WebViewScreen", "開始載入頁面: $url")
                                }
                                
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    Log.d("WebViewScreen", "頁面載入完成: $url")
                                }
                                
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    Log.e("WebViewScreen", "載入錯誤 - URL: ${request?.url}, 錯誤代碼: ${error?.errorCode}, 描述: ${error?.description}")
                                }
                                
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    // 確保所有連結都在 WebView 內開啟
                                    return false
                                }
                            }
                            
                            // WebView 設定
                            settings.apply {
                                javaScriptEnabled = true // 啟用 JavaScript
                                domStorageEnabled = true // 啟用 DOM 儲存
                                loadWithOverviewMode = true // 載入時縮放至適合螢幕
                                useWideViewPort = true // 啟用寬視窗
                                builtInZoomControls = true // 啟用縮放控制
                                displayZoomControls = false // 隱藏縮放按鈕
                                setSupportZoom(true) // 支援縮放
                                
                                // 設定 User-Agent 為標準桌面瀏覽器
                                userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                
                                // 混合內容設定（允許 HTTPS 頁面載入 HTTP 資源）
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            }
                            
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
