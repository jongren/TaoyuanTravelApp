# 桃園景點+ (Taoyuan Travel)

一個專為桃園旅遊設計的 Android 應用程式，基於桃園觀光旅遊網 Open API 所打造，提供最新旅遊資訊、熱門景點推薦和 AI 智能行程規劃的完整旅遊體驗

## 📱 最新版本
**版本**: v1.1 (Build 2)  
**發布日期**: 2025年1月16日  
**APK 大小**: 約 7.4 MB

### 📥 下載連結
- [📱 下載 APK (v1.1)](https://github.com/YOUR_USERNAME/TaoyuanTravel/releases/latest)
- [📋 查看完整更新日誌](https://github.com/YOUR_USERNAME/TaoyuanTravel/releases)

## 📱 應用特色
專案採用了最新的 Android 開發技術，包含 Jetpack Compose、MVI 架構、Hilt 依賴注入，力求打造一個結構清晰、易於維護和擴充的應用程式。

### 🌟 核心功能
- **🤖 AI 智能行程規劃**: 基於 Google Gemini AI 的個人化旅遊行程建議
  - 根據用戶需求智能推薦桃園景點
  - 支援多語言 AI 回應 (繁體中文/英文)
  - 動態語言同步，確保 AI 使用對應語言的景點資料
- **📰 最新消息**: 即時獲取桃園旅遊相關新聞和活動資訊
- **🏞️ 熱門景點**: 精選桃園必訪景點，包含詳細介紹和美麗圖片
- **📱 響應式設計**: 支援直向和橫向佈局，提供最佳瀏覽體驗
- **🌍 多語言支援**: 在 App 內即時切換繁體中文與英文，UI 介面與 API 內容會同步更新
- **🌐 網頁瀏覽**: 內建 WebView 瀏覽詳細資訊

### 🎨 設計亮點
- **Material Design 3**: 採用最新的 Material Design 設計語言
- **流暢動畫**: 豐富的進場動畫和互動反饋
- **雙欄式橫向佈局**: 充分利用橫向螢幕空間
- **現代化 UI**: 卡片式設計，視覺層次分明
- **組件化設計**: 高度模組化的 Composable 組件，提升可重用性
- **完善錯誤處理**: 統一的載入和錯誤狀態管理

### 🏗️ 架構特色
- **MVI 模式**: 採用 Model-View-Intent 架構，確保單向資料流
- **狀態集中管理**: 使用 StateFlow 進行響應式狀態管理
- **組件化設計**: 將大型 Composable 拆分為小型、可重用的組件
- **錯誤處理機制**: 統一的錯誤處理和載入狀態管理
- **代碼品質**: 完整的 KDoc 註解和統一的 coding style

### 開發技術
- **語言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架構模式**: MVI (Model-View-Intent) + MVVM
- **狀態管理**: StateFlow + Compose State
- **導航**: Navigation Compose
- **圖片載入**: Coil
- **網路請求**: Retrofit + OkHttp
- **依賴注入**: Hilt
- **AI 整合**: Google Gemini API
- **錯誤處理**: runCatching + 統一錯誤狀態管理
- **代碼混淆**: ProGuard/R8 優化

## 📂 專案結構

```
app/src/main/java/com/example/taoyuantravel/
├── data/                   # 資料層
│   ├── model/              # 資料模型
│   ├── repository/         # 資料倉庫 (包含 Gemini AI)
│   ├── remote/             # API 介面與 DTO
│   └── api/                # API 服務定義
├── ui/                     # UI 層
│   ├── home/               # 首頁 (景點列表、語言切換)
│   ├── detail/             # 景點詳情頁面
│   ├── planner/            # AI 行程規劃頁面
│   ├── webview/            # 網頁瀏覽頁面
│   ├── navigation/         # 導航配置
│   └── theme/              # 主題設定
├── utils/                  # 工具類別
└── MainActivity.kt         # 主要活動
```

## 🚀 安裝與運行

### 環境需求
- Android Studio Hedgehog | 2023.1.1 或更新版本
- Kotlin 1.9.0+
- Android SDK 34
- 最低支援 Android 7.0 (API 24)
- Google Gemini API Key (用於 AI 功能)

### 安裝步驟
1. Clone 此專案到本地
```bash
git clone https://github.com/YOUR_USERNAME/TaoyuanTravel.git
```

2. 在專案根目錄建立 `local.properties` 檔案，並添加您的 API Key：
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

3. 使用 Android Studio 開啟專案

4. 等待 Gradle 同步完成

5. 連接 Android 裝置或啟動模擬器

6. 點擊 Run 按鈕執行應用程式

### 📱 直接安裝 APK
如果您只想使用應用程式而不進行開發，可以直接下載並安裝 Release APK：
1. 前往 [Releases 頁面](https://github.com/YOUR_USERNAME/TaoyuanTravel/releases)
2. 下載最新版本的 APK 檔案
3. 在 Android 裝置上安裝 APK (需要允許未知來源安裝)

## 📱 功能展示

### 首頁 (直向模式)
- 頂部導航欄包含設定和語言切換
- AI行程規劃師，輸入喜好制定客製化行程
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





**桃園景點+** - 探索桃園之美，從這裡開始！ 🌸