package com.example.taoyuantravel.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.Declaration
import com.example.taoyuantravel.data.model.InfoWrapper
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.data.repository.TaoyuanTravelRepository
import com.example.taoyuantravel.ui.model.Language
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<TaoyuanTravelRepository>()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock successful responses
        val mockNewsResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "1",
                        updated = "2024-01-01"
                    ),
                    data = listOf(
                        News(
                            id = "1",
                            name = "測試新聞",
                            description = "測試描述",
                            posted = "2024-01-01",
                            images = null,
                            url = "https://example.com/news",
                            links = null
                        )
                    )
                )
            )
        )
        
        val mockAttractionsResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "1",
                        updated = "2024-01-01"
                    ),
                    data = listOf(
                        Attraction(
                            id = 1,
                            name = "測試景點",
                            introduction = "測試介紹",
                            openTime = "09:00-17:00",
                            address = "測試地址",
                            images = null,
                            links = null
                        )
                    )
                )
            )
        )
        
        coEvery { mockRepository.getNews(any(), any()) } returns mockNewsResponse
        coEvery { mockRepository.getAttractions(any(), any()) } returns mockAttractionsResponse
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `初始狀態應該正確設置`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        
        val initialState = viewModel.state.first()
        
        assertTrue(initialState.isLoading)
        assertEquals(Language.ZH_TW, initialState.selectedLanguage)
        assertEquals(Language.values().toList(), initialState.languages)
        assertTrue(initialState.news.isEmpty())
        assertTrue(initialState.attractions.isEmpty())
        assertNull(initialState.error)
    }

    @Test
    fun `loadData 應該成功載入資料`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        val finalState = viewModel.state.first()
        
        assertFalse(finalState.isLoading)
        assertEquals(1, finalState.news.size)
        assertEquals(1, finalState.attractions.size)
        assertEquals("測試新聞", finalState.news.first().name)
        assertEquals("測試景點", finalState.attractions.first().name)
        assertNull(finalState.error)
        
        coVerify { mockRepository.getNews("zh-tw", 1) }
        coVerify { mockRepository.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `ChangeLanguage 事件應該更新語言並重新載入資料`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        // 切換到英文
        viewModel.onEvent(HomeEvent.ChangeLanguage("en"))
        advanceUntilIdle()
        
        val state = viewModel.state.first()
        assertEquals(Language.EN, state.selectedLanguage)
        
        // 驗證使用新語言重新載入資料
        coVerify { mockRepository.getNews("en", 1) }
        coVerify { mockRepository.getAttractions("en", 1) }
    }

    @Test
    fun `ChangeLanguage 相同語言不應該觸發重新載入`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        // 清除之前的調用記錄
        coEvery { mockRepository.getNews(any(), any()) } returns Response.success(
            ApiResponse(infos = InfoWrapper(
                declaration = Declaration(
                    orgName = "Test",
                    siteName = "Test",
                    total = "0",
                    updated = "2024-01-01"
                ),
                data = emptyList()
            ))
        )
        coEvery { mockRepository.getAttractions(any(), any()) } returns Response.success(
            ApiResponse(infos = InfoWrapper(
                declaration = Declaration(
                    orgName = "Test",
                    siteName = "Test",
                    total = "0",
                    updated = "2024-01-01"
                ),
                data = emptyList()
            ))
        )
        
        // 嘗試設置相同的語言
        viewModel.onEvent(HomeEvent.ChangeLanguage("zh-tw"))
        advanceUntilIdle()
        
        // 驗證沒有額外的 API 調用
        coVerify(exactly = 1) { mockRepository.getNews("zh-tw", 1) }
        coVerify(exactly = 1) { mockRepository.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `LoadData 事件應該重新載入資料`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        // 觸發 LoadData 事件
        viewModel.onEvent(HomeEvent.LoadData)
        advanceUntilIdle()
        
        // 驗證資料被重新載入
        coVerify(atLeast = 2) { mockRepository.getNews("zh-tw", 1) }
        coVerify(atLeast = 2) { mockRepository.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `網路錯誤應該正確處理`() = runTest {
        // Mock 網路錯誤
        coEvery { mockRepository.getNews(any(), any()) } throws Exception("網路錯誤")
        coEvery { mockRepository.getAttractions(any(), any()) } throws Exception("網路錯誤")
        
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        val state = viewModel.state.first()
        
        assertFalse(state.isLoading)
        assertEquals("網路錯誤", state.error)
        assertTrue(state.news.isEmpty())
        assertTrue(state.attractions.isEmpty())
    }

    @Test
    fun `API 回應失敗應該返回空列表`() = runTest {
        // Mock 失敗的 API 回應
        coEvery { mockRepository.getNews(any(), any()) } returns Response.error(404, mockk(relaxed = true))
        coEvery { mockRepository.getAttractions(any(), any()) } returns Response.error(404, mockk(relaxed = true))
        
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        val state = viewModel.state.first()
        
        assertFalse(state.isLoading)
        assertTrue(state.news.isEmpty())
        assertTrue(state.attractions.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `無效語言代碼不應該改變狀態`() = runTest {
        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        val initialState = viewModel.state.first()
        
        // 嘗試設置無效的語言代碼
        viewModel.onEvent(HomeEvent.ChangeLanguage("invalid-lang"))
        advanceUntilIdle()
        
        val finalState = viewModel.state.first()
        
        // 語言應該保持不變
        assertEquals(initialState.selectedLanguage, finalState.selectedLanguage)
    }
}