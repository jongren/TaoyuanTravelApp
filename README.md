# 桃園景點+ (Taoyuan Travel)

一個專為桃園旅遊設計的 Android 應用程式，基於桃園觀光旅遊網 Open API 所打造，提供最新旅遊資訊、熱門景點推薦和完整的旅遊體驗

## 📱 應用特色
專案採用了最新的 Android 開發技術，包含 Jetpack Compose、MVI 架構、Hilt 依賴注入，力求打造一個結構清晰、易於維護和擴充的應用程式。

### 🌟 核心功能
- **最新消息**: 即時獲取桃園旅遊相關新聞和活動資訊
- **熱門景點**: 精選桃園必訪景點，包含詳細介紹和美麗圖片
- **響應式設計**: 支援直向和橫向佈局，提供最佳瀏覽體驗
- **多語言支援**: 在 App 內即時切換繁體中文與英文，UI 介面與 API 內容會同步更新。
- **網頁瀏覽**: 內建 WebView 瀏覽詳細資訊

### 🎨 設計亮點
- **Material Design 3**: 採用最新的 Material Design 設計語言
- **流暢動畫**: 豐富的進場動畫和互動反饋
- **雙欄式橫向佈局**: 充分利用橫向螢幕空間
- **現代化 UI**: 卡片式設計，視覺層次分明

## 🛠 技術架構

### 開發技術
- **語言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架構模式**: MVVM (Model-View-ViewModel)
- **導航**: Navigation Compose
- **圖片載入**: Coil
- **網路請求**: Retrofit + OkHttp
- **依賴注入**: Hilt

## 📂 專案結構

```
app/src/main/java/com/example/taoyuantravel/
├── data/                   # 資料層
│   ├── model/              # 資料模型
│   ├── repository/         # 資料倉庫
│   └── api/                # API 介面
├── ui/                     # UI 層
│   ├── (home, detail, webview)/ # 各個功能畫面
│   ├── model/              #  UI 專用的資料模型
│   ├── navigation/         # 管理所有畫面的路由與切換
│   └── theme/              # 主題設定
├── navigation/             # 導航配置
└── MainActivity.kt         # 主要活動
```

## 🚀 安裝與運行
- Clone 此專案。
- 使用 Android Studio 開啟。
- 建置並執行 App。

### 環境需求
- Android Studio Hedgehog | 2023.1.1 或更新版本
- Android SDK API 24 (Android 7.0) 或以上
- Kotlin 1.9.0 或更新版本

## 📱 功能展示

### 首頁 (直向模式)
- 頂部導航欄包含設定和語言切換
- 最新消息水平滾動列表
- 熱門景點垂直列表
- 流暢的進場動畫效果

### 首頁 (橫向模式)
- 雙欄式佈局設計
- 左欄：最新消息 (40% 寬度)
- 右欄：熱門景點網格 (60% 寬度)
- 自動響應螢幕方向變化

### 設定頁面
- 語言切換功能
- 應用程式相關設定
- 簡潔的設定介面

### WebView 瀏覽
- 內建網頁瀏覽器
- 支援返回導航
- 流暢的頁面載入

### 動畫系統
- 使用 `animateFloatAsState` 實現縮放動畫
- `animateDpAsState` 控制陰影變化
- `spring` 動畫提供自然的彈性效果

### 圖片載入優化
- 使用 Coil 進行異步圖片載入
- 支援 crossfade 過渡效果
- 漸層遮罩增強文字可讀性


### API 端點
本專案所有資料均來自：
桃園觀光旅遊網 Open API

## 📄 授權條款

## 🤝 貢獻指南

## 📞 聯絡資訊



**桃園景點+** - 探索桃園之美，從這裡開始！ 🌸