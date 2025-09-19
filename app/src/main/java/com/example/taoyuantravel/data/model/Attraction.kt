package com.example.taoyuantravel.data.model

import com.google.gson.annotations.SerializedName

/**
 * 景點資料模型
 */
data class Attraction(
    @SerializedName("Id")
    val id: Int = 0,

    @SerializedName("Name")
    val name: String = "",

    @SerializedName("Description")
    val introduction: String = "", // JSON 的 Description 對應到我們 App 的 introduction

    @SerializedName("Open-Time")
    val openTime: String = "",

    @SerializedName("Address")
    val address: String = "",

    @SerializedName("Images")
    val images: ImageWrapper? = null,

    @SerializedName("Links")
    val links: LinkWrapper? = null,

    // 地理座標資訊 (透過 Geocoding API 取得)
    @SerializedName("Latitude")
    val latitude: Double? = null,

    @SerializedName("Longitude")
    val longitude: Double? = null,

    // 景點分類 (用於地圖篩選)
    @SerializedName("Category")
    val category: String = "景點"
)