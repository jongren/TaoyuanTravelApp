package com.example.taoyuantravel.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用於匹配 API 回應的最外層 JSON 結構
 * 例如: { "Infos": { ... } }
 */
data class ApiResponse<T>(
    @SerializedName("Infos")
    val infos: InfoWrapper<T>
)

/**
 * 用於匹配 "Infos" 物件內部的結構
 * 例如: { "Declaration": {...}, "Info": [...] }
 */
data class InfoWrapper<T>(
    @SerializedName("Declaration")
    val declaration: Declaration,

    @SerializedName("Info")
    val data: List<T>
)

/**
 * 用於匹配 "Declaration" 物件的結構
 */
data class Declaration(
    @SerializedName("Orgname")
    val orgName: String,

    @SerializedName("SiteName")
    val siteName: String,

    @SerializedName("Total")
    val total: String,

    @SerializedName("Updated")
    val updated: String
)

