package com.example.taoyuantravel.data.repository

import com.example.taoyuantravel.BuildConfig
import com.example.taoyuantravel.data.model.ItineraryResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini API Repository，負責與 Google Gemini AI 服務進行通訊
 * 提供行程規劃相關的 AI 功能
 */
@Singleton
class GeminiRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    
    companion object {
        private const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent"
        private val API_KEY = BuildConfig.GEMINI_API_KEY
    }
    
    /**
     * 生成旅遊行程
     * @param userPrompt 使用者的旅遊偏好
     * @param attractionsJson 從桃園觀光 API 取得的景點列表（JSON 字串）
     * @return AI 生成的行程結果
     */
    suspend fun generateItinerary(
        userPrompt: String,
        attractionsJson: String
    ): ItineraryResponse = withContext(Dispatchers.IO) {
        
        val systemInstruction = """
            你是一位專業的桃園旅遊規劃師。你的任務是根據使用者提供的[旅遊偏好]和一份[景點列表]，規劃一份合理、有趣、且行程順暢的旅遊計畫。
            
            你的回覆**必須**嚴格遵循以下 JSON 格式，不得包含任何額外的解釋或 Markdown 語法：
            {
              "title": "為您規劃的桃園一日遊",
              "summary": "這是一條結合自然風光與咖啡小憩的輕鬆行程，適合悠閒的午後時光。",
              "steps": [
                {
                  "time": "13:00 - 15:00",
                  "activity": "享受湖光山色",
                  "location": "石門水庫",
                  "description": "您可以沿著壩頂步道散步，欣賞壯麗的水庫風景。"
                },
                {
                  "time": "15:30 - 17:00",
                  "activity": "品味老街風情與手沖咖啡",
                  "location": "大溪老街",
                  "description": "穿梭在巴洛克風格的街屋中，找一家有特色的咖啡廳稍作休息。"
                },
                {
                  "time": "17:00 - ",
                  "activity": "欣賞夕陽與漁港景致",
                  "location": "永安漁港",
                  "description": "在彩虹橋上等待日落，為今天的旅程畫下完美句點。"
                }
              ]
            }
        """.trimIndent()
        
        val userContent = """
            [旅遊偏好]: $userPrompt
            
            [景點列表]: $attractionsJson
            
            請根據以上資訊為我規劃一份桃園旅遊行程。
        """.trimIndent()
        
        val requestBody = JSONObject().apply {
            put("system_instruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userContent)
                        })
                    })
                })
            })
        }
        
        if (API_KEY.isBlank() || API_KEY == "\"\"") {
            android.util.Log.e("GeminiRepository", "Gemini API 金鑰未配置")
            throw Exception("Gemini API 金鑰未配置，請檢查 local.properties 文件")
        }
        
        val request = Request.Builder()
            .url("$GEMINI_API_URL?key=$API_KEY")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()
        
        val response = okHttpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "無錯誤詳情"
            android.util.Log.e("GeminiRepository", "Gemini API 呼叫失敗: ${response.code} ${response.message}")
            throw Exception("Gemini API 呼叫失敗: ${response.code} ${response.message}\n錯誤詳情: $errorBody")
        }
        
        val responseBody = response.body?.string() ?: throw Exception("回應內容為空")
        
        val jsonResponse = JSONObject(responseBody)
        val candidates = jsonResponse.getJSONArray("candidates")
        
        if (candidates.length() == 0) {
            throw Exception("Gemini API 沒有返回任何候選結果")
        }
        
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        
        if (parts.length() == 0) {
            throw Exception("Gemini API 回應格式錯誤")
        }
        
        val generatedText = parts.getJSONObject(0).getString("text")
        
        try {
            val cleanedJson = extractJsonFromText(generatedText)
            
            if (cleanedJson.isBlank() || cleanedJson == "{}") {
                throw Exception("AI 回應中沒有找到有效的 JSON 內容")
            }
            
            val result = gson.fromJson(cleanedJson, ItineraryResponse::class.java)
            
            if (result == null) {
                android.util.Log.e("GeminiRepository", "JSON 解析結果為空")
                throw Exception("JSON 解析結果為空")
            }
            
            return@withContext result
        } catch (e: Exception) {
            android.util.Log.e("GeminiRepository", "無法解析 AI 回應: ${e.message}")
            throw Exception("無法解析 AI 回應的 JSON 格式: ${e.message}\n清理後的 JSON: ${extractJsonFromText(generatedText)}\n原始回應: $generatedText")
        }
    }
    
    /**
     * 從AI回應文字中提取JSON內容
     */
    private fun extractJsonFromText(text: String): String {
        // 移除可能的 Markdown 代碼塊標記
        var cleanText = text.trim()
        
        // 移除 ```json 和 ``` 標記
        cleanText = cleanText.replace("```json", "").replace("```", "")
        
        // 尋找第一個 { 和最後一個 }
        val startIndex = cleanText.indexOf('{')
        val endIndex = cleanText.lastIndexOf('}')
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return cleanText.substring(startIndex, endIndex + 1)
        }
        
        // 如果找不到完整的JSON結構，返回原始文字
        return cleanText
    }
}