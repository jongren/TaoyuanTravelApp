package com.example.taoyuantravel.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taoyuantravel.ui.theme.TaoyuanTravelTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            TaoyuanTravelTheme {
                NavGraph(navController = navController)
            }
        }
    }

    @Test
    fun navGraph_驗證起始目的地() {
        // 驗證起始畫面是 Home
        composeTestRule.onNodeWithText("桃園旅遊")
            .assertIsDisplayed()
        
        // 驗證當前路由
        assert(navController.currentBackStackEntry?.destination?.route == Screen.Home.route)
    }

    @Test
    fun navGraph_導航到詳細頁面() {
        // 模擬點擊新聞項目導航到詳細頁面
        // 注意：這需要在實際的 HomeScreen 中有可點擊的項目
        
        // 首先確認在 Home 頁面
        assert(navController.currentBackStackEntry?.destination?.route == Screen.Home.route)
        
        // 手動導航到詳細頁面進行測試
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        
        // 等待導航完成並驗證
        composeTestRule.waitForIdle()
        
        // 驗證導航到詳細頁面
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assert(currentRoute?.contains(Screen.Detail.route) == true)
    }

    @Test
    fun navGraph_導航到WebView頁面() {
        // 手動導航到 WebView 頁面
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.WebView.route}?url=https://example.com")
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證導航到 WebView 頁面
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assert(currentRoute?.contains(Screen.WebView.route) == true)
    }

    @Test
    fun navGraph_返回導航功能() {
        // 導航到詳細頁面
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證在詳細頁面
        val detailRoute = navController.currentBackStackEntry?.destination?.route
        assert(detailRoute?.contains(Screen.Detail.route) == true)
        
        // 返回上一頁
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證回到 Home 頁面
        assert(navController.currentBackStackEntry?.destination?.route == Screen.Home.route)
    }

    @Test
    fun navGraph_深層導航測試() {
        // 測試多層導航
        
        // 1. 導航到詳細頁面
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        composeTestRule.waitForIdle()
        
        // 2. 從詳細頁面導航到 WebView
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.WebView.route}?url=https://example.com")
        }
        composeTestRule.waitForIdle()
        
        // 驗證在 WebView 頁面
        val webViewRoute = navController.currentBackStackEntry?.destination?.route
        assert(webViewRoute?.contains(Screen.WebView.route) == true)
        
        // 3. 返回到詳細頁面
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        
        // 驗證回到詳細頁面
        val detailRoute = navController.currentBackStackEntry?.destination?.route
        assert(detailRoute?.contains(Screen.Detail.route) == true)
        
        // 4. 返回到 Home 頁面
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        
        // 驗證回到 Home 頁面
        assert(navController.currentBackStackEntry?.destination?.route == Screen.Home.route)
    }

    @Test
    fun navGraph_參數傳遞測試() {
        // 測試帶參數的導航
        val testId = "123"
        val testType = "attraction"
        
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/$testId/$testType")
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證參數正確傳遞
        val currentBackStackEntry = navController.currentBackStackEntry
        val id = currentBackStackEntry?.arguments?.getString("id")
        val type = currentBackStackEntry?.arguments?.getString("type")
        
        assert(id == testId)
        assert(type == testType)
    }

    @Test
    fun navGraph_URL參數測試() {
        // 測試 WebView 的 URL 參數
        val testUrl = "https://www.example.com/test"
        
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.WebView.route}?url=$testUrl")
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證 URL 參數正確傳遞
        val currentBackStackEntry = navController.currentBackStackEntry
        val url = currentBackStackEntry?.arguments?.getString("url")
        
        assert(url == testUrl)
    }

    @Test
    fun navGraph_無效路由處理() {
        // 測試導航到無效路由
        val initialRoute = navController.currentBackStackEntry?.destination?.route
        
        composeTestRule.runOnUiThread {
            try {
                navController.navigate("invalid_route")
            } catch (e: Exception) {
                // 預期會有異常或保持在原路由
            }
        }
        
        composeTestRule.waitForIdle()
        
        // 驗證仍在原始路由或處理了無效路由
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        // 應該仍在 Home 頁面或有適當的錯誤處理
        assert(currentRoute == initialRoute || currentRoute == Screen.Home.route)
    }

    @Test
    fun navGraph_BackStack管理() {
        // 測試 BackStack 的正確管理
        
        // 初始狀態：只有 Home
        assert(navController.backQueue.size >= 1)
        
        // 導航到詳細頁面
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        composeTestRule.waitForIdle()
        
        // BackStack 應該增加
        val stackSizeAfterNavigation = navController.backQueue.size
        assert(stackSizeAfterNavigation >= 2)
        
        // 返回
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        
        // BackStack 應該減少
        val stackSizeAfterPop = navController.backQueue.size
        assert(stackSizeAfterPop < stackSizeAfterNavigation)
    }

    @Test
    fun navGraph_多次相同導航處理() {
        // 測試多次導航到相同目的地的處理
        
        // 第一次導航
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        composeTestRule.waitForIdle()
        
        val stackSizeAfterFirst = navController.backQueue.size
        
        // 第二次導航到相同目的地
        composeTestRule.runOnUiThread {
            navController.navigate("${Screen.Detail.route}/1/news")
        }
        composeTestRule.waitForIdle()
        
        val stackSizeAfterSecond = navController.backQueue.size
        
        // 驗證 BackStack 的行為（可能會增加或保持不變，取決於導航配置）
        assert(stackSizeAfterSecond >= stackSizeAfterFirst)
    }
}