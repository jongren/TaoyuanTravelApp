package com.example.taoyuantravel.data.model

import com.google.gson.annotations.SerializedName

/**
 * 景點資料模型
 */
data class Attraction(
    val id: Int,
    val name: String,
    val introduction: String,
    val address: String,
    val tel: String,
    @SerializedName("open_time") // API欄位與變數名稱不同，需特別指定
    val openTime: String,
    val url: String,
    val images: List<Image>
)

/**
 * 圖片資料模型
 */
data class Image(
    val src: String,
    val subject: String
)
