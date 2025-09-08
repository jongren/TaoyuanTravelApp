package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.data.model.ApiResponse
import com.example.taoyuantravel.data.model.Attraction
import com.example.taoyuantravel.data.model.Declaration
import com.example.taoyuantravel.data.model.InfoWrapper
import com.example.taoyuantravel.data.model.News
import com.example.taoyuantravel.data.source.remote.api.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class TaoyuanTravelRepositoryTest {

    private val mockApiService = mockk<ApiService>()
    private lateinit var repository: TaoyuanTravelRepository

    @Before
    fun setup() {
        repository = TaoyuanTravelRepositoryImpl(mockApiService)
    }

    @Test
    fun `getNews 成功回應應該返回正確資料`() = runTest {
        // Arrange
        val mockNews = listOf(
            News(
                id = "1",
                name = "測試新聞1",
                description = "測試描述1",
                posted = "2024-01-01",
                images = null,
                url = "https://example.com/news1",
                links = null
            ),
            News(
                id = "2",
                name = "測試新聞2",
                description = "測試描述2",
                posted = "2024-01-02",
                images = null,
                url = "https://example.com/news2",
                links = null
            )
        )
        
        val mockResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "1",
                        updated = "2024-01-01"
                    ),
                    data = mockNews
                )
            )
        )
        
        coEvery { mockApiService.getNews("zh-tw", 1) } returns mockResponse

        // Act
        val result = repository.getNews("zh-tw", 1)

        // Assert
        assertTrue(result.isSuccessful)
        assertEquals(2, result.body()?.infos?.data?.size)
        assertEquals("測試新聞1", result.body()?.infos?.data?.get(0)?.name)
        assertEquals("測試新聞2", result.body()?.infos?.data?.get(1)?.name)
        
        coVerify { mockApiService.getNews("zh-tw", 1) }
    }

    @Test
    fun `getNews 失敗回應應該返回錯誤`() = runTest {
        // Arrange
        val errorBody = "Not Found".toResponseBody("text/plain".toMediaTypeOrNull())
        val errorResponse = Response.error<ApiResponse<News>>(404, errorBody)
        
        coEvery { mockApiService.getNews("zh-tw", 1) } returns errorResponse

        // Act
        val result = repository.getNews("zh-tw", 1)

        // Assert
        assertFalse(result.isSuccessful)
        assertEquals(404, result.code())
        
        coVerify { mockApiService.getNews("zh-tw", 1) }
    }

    @Test
    fun `getNews 網路異常應該拋出例外`() = runTest {
        // Arrange
        coEvery { mockApiService.getNews("zh-tw", 1) } throws IOException("網路連接失敗")

        // Act & Assert
        try {
            repository.getNews("zh-tw", 1)
            assert(false) { "應該拋出 IOException" }
        } catch (e: IOException) {
            assertEquals("網路連接失敗", e.message)
        }
        
        coVerify { mockApiService.getNews("zh-tw", 1) }
    }

    @Test
    fun `getAttractions 成功回應應該返回正確資料`() = runTest {
        // Arrange
        val mockAttractions = listOf(
            Attraction(
                id = 1,
                name = "測試景點1",
                introduction = "測試介紹1",
                openTime = "09:00-17:00",
                address = "測試地址1",
                images = null,
                links = null
            ),
            Attraction(
                id = 2,
                name = "測試景點2",
                introduction = "測試介紹2",
                openTime = "10:00-18:00",
                address = "測試地址2",
                images = null,
                links = null
            )
        )
        
        val mockResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "1",
                        updated = "2024-01-01"
                    ),
                    data = mockAttractions
                )
            )
        )
        
        coEvery { mockApiService.getAttractions("zh-tw", 1) } returns mockResponse

        // Act
        val result = repository.getAttractions("zh-tw", 1)

        // Assert
        assertTrue(result.isSuccessful)
        assertEquals(2, result.body()?.infos?.data?.size)
        assertEquals("測試景點1", result.body()?.infos?.data?.get(0)?.name)
        assertEquals("測試景點2", result.body()?.infos?.data?.get(1)?.name)
        
        coVerify { mockApiService.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `getAttractions 失敗回應應該返回錯誤`() = runTest {
        // Arrange
        val errorBody = "Internal Server Error".toResponseBody("text/plain".toMediaTypeOrNull())
        val errorResponse = Response.error<ApiResponse<Attraction>>(500, errorBody)
        
        coEvery { mockApiService.getAttractions("zh-tw", 1) } returns errorResponse

        // Act
        val result = repository.getAttractions("zh-tw", 1)

        // Assert
        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
        
        coVerify { mockApiService.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `getAttractions 網路異常應該拋出例外`() = runTest {
        // Arrange
        coEvery { mockApiService.getAttractions("zh-tw", 1) } throws IOException("連接超時")

        // Act & Assert
        try {
            repository.getAttractions("zh-tw", 1)
            assert(false) { "應該拋出 IOException" }
        } catch (e: IOException) {
            assertEquals("連接超時", e.message)
        }
        
        coVerify { mockApiService.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `getNews 不同語言參數測試`() = runTest {
        // Arrange
        val languages = listOf("zh-tw", "en", "ja")
        val mockResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<News>()
                )
            )
        )
        
        languages.forEach { lang ->
            coEvery { mockApiService.getNews(lang, 1) } returns mockResponse
        }

        // Act & Assert
        languages.forEach { lang ->
            val result = repository.getNews(lang, 1)
            assertTrue(result.isSuccessful)
            coVerify { mockApiService.getNews(lang, 1) }
        }
    }

    @Test
    fun `getAttractions 不同頁數參數測試`() = runTest {
        // Arrange
        val pages = listOf(1, 2, 3)
        val mockResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<Attraction>()
                )
            )
        )
        
        pages.forEach { page ->
            coEvery { mockApiService.getAttractions("zh-tw", page) } returns mockResponse
        }

        // Act & Assert
        pages.forEach { page ->
            val result = repository.getAttractions("zh-tw", page)
            assertTrue(result.isSuccessful)
            coVerify { mockApiService.getAttractions("zh-tw", page) }
        }
    }

    @Test
    fun `getNews 空回應處理`() = runTest {
        // Arrange
        val emptyResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<News>()
                )
            )
        )
        
        coEvery { mockApiService.getNews("zh-tw", 1) } returns emptyResponse

        // Act
        val result = repository.getNews("zh-tw", 1)

        // Assert
        assertTrue(result.isSuccessful)
        assertTrue(result.body()?.infos?.data?.isEmpty() == true)
        
        coVerify { mockApiService.getNews("zh-tw", 1) }
    }

    @Test
    fun `getAttractions 空回應處理`() = runTest {
        // Arrange
        val emptyResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<Attraction>()
                )
            )
        )
        
        coEvery { mockApiService.getAttractions("zh-tw", 1) } returns emptyResponse

        // Act
        val result = repository.getAttractions("zh-tw", 1)

        // Assert
        assertTrue(result.isSuccessful)
        assertTrue(result.body()?.infos?.data?.isEmpty() == true)
        
        coVerify { mockApiService.getAttractions("zh-tw", 1) }
    }

    @Test
    fun `多次調用相同 API 應該正確處理`() = runTest {
        // Arrange
        val mockResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<News>()
                )
            )
        )
        
        coEvery { mockApiService.getNews("zh-tw", 1) } returns mockResponse

        // Act
        repeat(3) {
            repository.getNews("zh-tw", 1)
        }

        // Assert
        coVerify(exactly = 3) { mockApiService.getNews("zh-tw", 1) }
    }

    @Test
    fun `併發請求應該正確處理`() = runTest {
        // Arrange
        val newsResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<News>()
                )
            )
        )
        
        val attractionsResponse = Response.success(
            ApiResponse(
                infos = InfoWrapper(
                    declaration = Declaration(
                        orgName = "Test",
                        siteName = "Test",
                        total = "0",
                        updated = "2024-01-01"
                    ),
                    data = emptyList<Attraction>()
                )
            )
        )
        
        coEvery { mockApiService.getNews("zh-tw", 1) } returns newsResponse
        coEvery { mockApiService.getAttractions("zh-tw", 1) } returns attractionsResponse

        // Act
        val newsResult = repository.getNews("zh-tw", 1)
        val attractionsResult = repository.getAttractions("zh-tw", 1)

        // Assert
        assertTrue(newsResult.isSuccessful)
        assertTrue(attractionsResult.isSuccessful)
        
        coVerify { mockApiService.getNews("zh-tw", 1) }
        coVerify { mockApiService.getAttractions("zh-tw", 1) }
    }
}