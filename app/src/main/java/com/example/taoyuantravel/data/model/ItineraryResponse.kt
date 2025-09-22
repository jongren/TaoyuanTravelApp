package com.example.taoyuantravel.data.model

import com.google.gson.annotations.SerializedName

/**
 * AI 行程規劃回應的數據模型
 */
data class ItineraryResponse(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("summary")
    val summary: String,
    
    @SerializedName("steps")
    val steps: List<ItineraryStep>
)

/**
 * 行程步驟的數據模型
 */
data class ItineraryStep(
    @SerializedName("time")
    val time: String,
    
    @SerializedName("activity")
    val activity: String,
    
    @SerializedName("location")
    val location: String,
    
    @SerializedName("description")
    val description: String
)