package com.example.taoyuantravel.data.repository

import android.content.Context
import com.example.taoyuantravel.BuildConfig
import com.example.taoyuantravel.R
import com.example.taoyuantravel.data.model.ItineraryResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini API Repository，負責與 Google Gemini AI 服務進行通訊
 * 提供行程規劃相關的 AI 功能
 */
@Singleton
class GeminiRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
    @ApplicationContext private val context: Context
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
        
        val systemInstruction = context.getString(R.string.ai_system_instruction)
        val userContent = context.getString(R.string.ai_user_content_template, userPrompt, attractionsJson)
        
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
            android.util.Log.e("GeminiRepository", "Gemini API key not configured")
            throw Exception(context.getString(R.string.error_gemini_api_key_not_configured))
        }
        
        val request = Request.Builder()
            .url("$GEMINI_API_URL?key=$API_KEY")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()
        
        val response = okHttpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "No error details"
            android.util.Log.e("GeminiRepository", "Gemini API call failed: ${response.code} ${response.message}")
            throw Exception("${context.getString(R.string.error_gemini_api_call_failed)}: ${response.code} ${response.message}\nError details: $errorBody")
        }
        
        val responseBody = response.body?.string() ?: throw Exception("Response body is empty")
        
        val jsonResponse = JSONObject(responseBody)
        val candidates = jsonResponse.getJSONArray("candidates")
        
        if (candidates.length() == 0) {
            throw Exception(context.getString(R.string.error_gemini_no_candidates))
        }
        
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        
        if (parts.length() == 0) {
            throw Exception(context.getString(R.string.error_gemini_response_format))
        }
        
        val generatedText = parts.getJSONObject(0).getString("text")
        
        try {
            val cleanedJson = extractJsonFromText(generatedText)
            
            if (cleanedJson.isBlank() || cleanedJson == "{}") {
                throw Exception(context.getString(R.string.error_ai_no_valid_json))
            }
            
            val result = gson.fromJson(cleanedJson, ItineraryResponse::class.java)
            
            if (result == null) {
                android.util.Log.e("GeminiRepository", "JSON parsing result is null")
                throw Exception(context.getString(R.string.error_json_parse_null))
            }
            
            return@withContext result
        } catch (e: Exception) {
            android.util.Log.e("GeminiRepository", "Unable to parse AI response: ${e.message}")
            throw Exception("${context.getString(R.string.error_json_parse_failed)}: ${e.message}\nCleaned JSON: ${extractJsonFromText(generatedText)}\nOriginal response: $generatedText")
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