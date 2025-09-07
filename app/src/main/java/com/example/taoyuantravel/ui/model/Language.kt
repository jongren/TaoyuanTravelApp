package com.example.taoyuantravel.ui.model

import java.util.Locale

/**
 * 定義 App 支援的多語系枚舉。
 *
 * @param code 用於 API 請求的語言代碼。
 * @param displayName 在 UI 上顯示的語言名稱。
 * @param locale 對應的 Java Locale 物件，用於變更 App 的 UI 語系。
 */
enum class Language(val code: String, val displayName: String, val locale: Locale) {
    ZH_TW("zh-tw", "繁體中文", Locale.TRADITIONAL_CHINESE),
    ZH_CN("zh-cn", "简体中文", Locale.SIMPLIFIED_CHINESE),
    EN("en", "English", Locale.ENGLISH),
    JA("ja", "日本語", Locale.JAPANESE),
    KO("ko", "한국어", Locale.KOREAN),
    ES("es", "Español", Locale("es")),
    ID("id", "Indonesia", Locale("in")),
    TH("th", "ภาษาไทย", Locale("th")),
    VI("vi", "Tiếng Việt", Locale("vi"))
}

