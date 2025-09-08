package com.example.taoyuantravel.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.ui.model.Language
import com.example.taoyuantravel.ui.theme.TaoyuanTravelTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleNews = listOf(
        News(
            id = 1,
            title = "測試新聞標題1",
            description = "測試新聞描述1",
            picture1 = "https://example.com/news1.jpg",
            url = "https://example.com/news1"
        ),
        News(
            id = 2,
            title = "測試新聞標題2",
            description = "測試新聞描述2",
            picture1 = "https://example.com/news2.jpg",
            url = "https://example.com/news2"
        )
    )

    private val sampleAttractions = listOf(
        Attraction(
            id = 1,
            name = "測試景點1",
            introduction = "測試景點介紹1",
            picture1 = "https://example.com/attraction1.jpg",
            address = "測試地址1",
            tel = "123-456-7890",
            openTime = "09:00-17:00",
            website = "https://example.com/attraction1"
        ),
        Attraction(
            id = 2,
            name = "測試景點2",
            introduction = "測試景點介紹2",
            picture1 = "https://example.com/attraction2.jpg",
            address = "測試地址2",
            tel = "987-654-3210",
            openTime = "10:00-18:00",
            website = "https://example.com/attraction2"
        )
    )

    @Test
    fun homeScreen_顯示載入狀態() {
        val loadingState = HomeState(
            isLoading = true,
            news = emptyList(),
            attractions = emptyList()
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = loadingState,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證載入指示器顯示
        composeTestRule.onNodeWithContentDescription("載入中")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_顯示錯誤狀態() {
        val errorState = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = emptyList(),
            error = "網路連接失敗"
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = errorState,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證錯誤訊息顯示
        composeTestRule.onNodeWithText("網路連接失敗")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_顯示新聞列表() {
        val successState = HomeState(
            isLoading = false,
            news = sampleNews,
            attractions = emptyList()
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = successState,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證新聞標題顯示
        composeTestRule.onNodeWithText("最新消息")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("測試新聞標題1")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("測試新聞描述1")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_顯示景點列表() {
        val successState = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = sampleAttractions
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = successState,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證景點標題顯示
        composeTestRule.onNodeWithText("熱門景點")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("測試景點1")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("測試景點介紹1")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_語言選擇器功能() {
        val state = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = emptyList(),
            selectedLanguage = Language.ZH_TW
        )

        var receivedEvent: HomeEvent? = null

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = state,
                    onEvent = { receivedEvent = it },
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 點擊語言選擇器
        composeTestRule.onNodeWithContentDescription("語言選擇")
            .performClick()

        // 選擇英文
        composeTestRule.onNodeWithText("English")
            .performClick()

        // 驗證事件被觸發
        assert(receivedEvent is HomeEvent.ChangeLanguage)
        assert((receivedEvent as HomeEvent.ChangeLanguage).langCode == "en")
    }

    @Test
    fun homeScreen_新聞項目點擊() {
        val state = HomeState(
            isLoading = false,
            news = sampleNews,
            attractions = emptyList()
        )

        var clickedNews: News? = null

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = state,
                    onEvent = {},
                    onNewsClick = { clickedNews = it },
                    onAttractionClick = {}
                )
            }
        }

        // 點擊第一個新聞項目
        composeTestRule.onNodeWithText("測試新聞標題1")
            .performClick()

        // 驗證點擊回調被觸發
        assert(clickedNews == sampleNews[0])
    }

    @Test
    fun homeScreen_景點項目點擊() {
        val state = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = sampleAttractions
        )

        var clickedAttraction: Attraction? = null

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = state,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = { clickedAttraction = it }
                )
            }
        }

        // 點擊第一個景點項目
        composeTestRule.onNodeWithText("測試景點1")
            .performClick()

        // 驗證點擊回調被觸發
        assert(clickedAttraction == sampleAttractions[0])
    }

    @Test
    fun homeScreen_滾動功能() {
        val largeNewsList = (1..10).map { index ->
            News(
                id = index,
                title = "新聞標題 $index",
                description = "新聞描述 $index",
                picture1 = "https://example.com/news$index.jpg",
                url = "https://example.com/news$index"
            )
        }

        val state = HomeState(
            isLoading = false,
            news = largeNewsList,
            attractions = emptyList()
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = state,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證第一個項目可見
        composeTestRule.onNodeWithText("新聞標題 1")
            .assertIsDisplayed()

        // 滾動到最後一個項目
        composeTestRule.onNodeWithTag("news_lazy_column")
            .performScrollToIndex(9)

        // 驗證最後一個項目可見
        composeTestRule.onNodeWithText("新聞標題 10")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_TopAppBar顯示正確() {
        val state = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = emptyList()
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = state,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證 TopAppBar 標題
        composeTestRule.onNodeWithText("桃園旅遊")
            .assertIsDisplayed()

        // 驗證語言選擇按鈕
        composeTestRule.onNodeWithContentDescription("語言選擇")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_空狀態顯示() {
        val emptyState = HomeState(
            isLoading = false,
            news = emptyList(),
            attractions = emptyList()
        )

        composeTestRule.setContent {
            TaoyuanTravelTheme {
                HomeScreen(
                    state = emptyState,
                    onEvent = {},
                    onNewsClick = {},
                    onAttractionClick = {}
                )
            }
        }

        // 驗證標題仍然顯示
        composeTestRule.onNodeWithText("最新消息")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("熱門景點")
            .assertIsDisplayed()
    }
}