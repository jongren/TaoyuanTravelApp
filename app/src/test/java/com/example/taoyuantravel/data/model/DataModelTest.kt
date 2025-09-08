package com.example.taoyuantravel.data.model

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue

class DataModelTest {

    @Test
    fun `News 模型應該正確創建和比較`() {
        val news1 = News(
            id = "1",
            name = "測試新聞標題",
            description = "測試新聞描述",
            posted = "2024-01-01",
            images = null,
            url = "https://example.com/news",
            links = null
        )
        
        val news2 = News(
            id = "1",
            name = "測試新聞標題",
            description = "測試新聞描述",
            posted = "2024-01-01",
            images = null,
            url = "https://example.com/news",
            links = null
        )
        
        val news3 = News(
            id = "2",
            name = "不同的新聞標題",
            description = "不同的新聞描述",
            posted = "2024-01-02",
            images = null,
            url = "https://example.com/different",
            links = null
        )
        
        // 測試相等性
        assertEquals(news1, news2)
        assertNotEquals(news1, news3)
        
        // 測試屬性
        assertEquals("1", news1.id)
        assertEquals("測試新聞標題", news1.name)
        assertEquals("測試新聞描述", news1.description)
        assertEquals("https://example.com/news", news1.url)
    }

    @Test
    fun `Attraction 模型應該正確創建和比較`() {
        val attraction1 = Attraction(
            id = 1,
            name = "測試景點",
            introduction = "測試景點介紹",
            openTime = "09:00-17:00",
            address = "測試地址",
            images = null,
            links = null
        )
        
        val attraction2 = Attraction(
            id = 1,
            name = "測試景點",
            introduction = "測試景點介紹",
            openTime = "09:00-17:00",
            address = "測試地址",
            images = null,
            links = null
        )
        
        val attraction3 = Attraction(
            id = 2,
            name = "不同景點",
            introduction = "不同景點介紹",
            openTime = "10:00-18:00",
            address = "不同地址",
            images = null,
            links = null
        )
        
        // 測試相等性
        assertEquals(attraction1, attraction2)
        assertNotEquals(attraction1, attraction3)
        
        // 測試屬性
        assertEquals(1, attraction1.id)
        assertEquals("測試景點", attraction1.name)
        assertEquals("測試景點介紹", attraction1.introduction)
        assertEquals("09:00-17:00", attraction1.openTime)
        assertEquals("測試地址", attraction1.address)
    }

    @Test
    fun `ApiResponse 模型應該正確處理嵌套結構`() {
        val newsList = listOf(
            News(
                id = "1",
                name = "新聞1",
                description = "描述1",
                posted = "2024-01-01",
                images = null,
                url = "url1",
                links = null
            ),
            News(
                id = "2",
                name = "新聞2",
                description = "描述2",
                posted = "2024-01-02",
                images = null,
                url = "url2",
                links = null
            )
        )
        
        val infos = InfoWrapper(
            declaration = Declaration(
                orgName = "Test",
                siteName = "Test",
                total = "2",
                updated = "2024-01-01"
            ),
            data = newsList
        )
        val apiResponse = ApiResponse(infos = infos)
        
        assertEquals(2, apiResponse.infos.data.size)
        assertEquals("新聞1", apiResponse.infos.data[0].name)
        assertEquals("新聞2", apiResponse.infos.data[1].name)
    }

    @Test
    fun `News 模型應該處理空值和邊界情況`() {
        val newsWithEmptyStrings = News(
            id = "0",
            name = "",
            description = "",
            posted = "",
            images = null,
            url = "",
            links = null
        )
        
        assertEquals("0", newsWithEmptyStrings.id)
        assertEquals("", newsWithEmptyStrings.name)
        assertEquals("", newsWithEmptyStrings.description)
        assertEquals("", newsWithEmptyStrings.posted)
        assertEquals("", newsWithEmptyStrings.url)
    }

    @Test
    fun `Attraction 模型應該處理空值和邊界情況`() {
        val attractionWithEmptyStrings = Attraction(
            id = 0,
            name = "",
            introduction = "",
            openTime = "",
            address = "",
            images = null,
            links = null
        )
        
        assertEquals(0, attractionWithEmptyStrings.id)
        assertEquals("", attractionWithEmptyStrings.name)
        assertEquals("", attractionWithEmptyStrings.introduction)
        assertEquals("", attractionWithEmptyStrings.openTime)
        assertEquals("", attractionWithEmptyStrings.address)
    }

    @Test
    fun `ApiResponse 應該處理空列表`() {
        val emptyInfos = InfoWrapper(
            declaration = Declaration(
                orgName = "Test",
                siteName = "Test",
                total = "0",
                updated = "2024-01-01"
            ),
            data = emptyList<News>()
        )
        val emptyApiResponse = ApiResponse(infos = emptyInfos)
        
        assertTrue(emptyApiResponse.infos.data.isEmpty())
        assertEquals(0, emptyApiResponse.infos.data.size)
    }

    @Test
    fun `News hashCode 和 equals 應該正確實現`() {
        val news1 = News(
            id = "1",
            name = "標題",
            description = "描述",
            posted = "2024-01-01",
            images = null,
            url = "網址",
            links = null
        )
        
        val news2 = News(
            id = "1",
            name = "標題",
            description = "描述",
            posted = "2024-01-01",
            images = null,
            url = "網址",
            links = null
        )
        
        val news3 = News(
            id = "2",
            name = "標題",
            description = "描述",
            posted = "2024-01-01",
            images = null,
            url = "網址",
            links = null
        )
        
        // 測試 equals
        assertTrue(news1 == news2)
        assertFalse(news1 == news3)
        
        // 測試 hashCode
        assertEquals(news1.hashCode(), news2.hashCode())
        assertNotEquals(news1.hashCode(), news3.hashCode())
    }

    @Test
    fun `Attraction hashCode 和 equals 應該正確實現`() {
        val attraction1 = Attraction(
            id = 1,
            name = "景點",
            introduction = "介紹",
            openTime = "時間",
            address = "地址",
            images = null,
            links = null
        )
        
        val attraction2 = Attraction(
            id = 1,
            name = "景點",
            introduction = "介紹",
            openTime = "時間",
            address = "地址",
            images = null,
            links = null
        )
        
        val attraction3 = Attraction(
            id = 2,
            name = "景點",
            introduction = "介紹",
            openTime = "時間",
            address = "地址",
            images = null,
            links = null
        )
        
        // 測試 equals
        assertTrue(attraction1 == attraction2)
        assertFalse(attraction1 == attraction3)
        
        // 測試 hashCode
        assertEquals(attraction1.hashCode(), attraction2.hashCode())
        assertNotEquals(attraction1.hashCode(), attraction3.hashCode())
    }
}