package com.example.taoyuantravel.data.model

import com.google.gson.annotations.SerializedName

/**
 * 代表 API 回傳的單一最新消息的資料模型。
 * 欄位名稱使用 @SerializedName 來對應 JSON 中的 key。
 */
data class News(
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Posted") val posted: String,
    @SerializedName("Images") val images: ImageWrapper?,
    @SerializedName("TYWebsite") val url: String, // 修正：對應到 TYWebsite 欄位
    @SerializedName("Links") val links: LinkWrapper?
)

