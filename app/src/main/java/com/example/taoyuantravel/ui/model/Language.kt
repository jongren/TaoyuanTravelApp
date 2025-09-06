package com.example.taoyuantravel.ui.model

/**
 * 定義 App 支援的多語系枚舉。
 *
 * @param code 用於 API 請求的語言代碼。
 * @param displayName 在 UI 上顯示的語言名稱。
 */
enum class Language(val code: String, val displayName: String) {
    ZH_TW("zh-tw", "繁體中文"),
    ZH_CN("zh-cn", "简体中文"),
    EN("en", "English"),
    JA("ja", "日本語"),
    KO("ko", "한국어"),
    ES("es", "Español"),
    ID("id", "Indonesia"),
    TH("th", "ภาษาไทย"),
    VI("vi", "Tiếng Việt")
}

