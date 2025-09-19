# GitHub Release v1.1 建立指南

## 📋 準備工作 ✅
- [x] 版本配置確認 (versionCode: 2, versionName: 1.1)
- [x] Release APK 建置完成 (app-release.apk, 7.4MB)
- [x] ProGuard/R8 混淆配置優化
- [x] 功能測試完成
- [x] 文檔更新完成 (README.md, RELEASE_NOTES_v1.1.md)
- [x] Git tag v1.1 已建立並推送

## 🚀 GitHub Release 建立步驟

### 1. 前往 GitHub Repository
開啟瀏覽器，前往：
```
https://github.com/jongren/TaoyuanTravelApp
```

### 2. 建立新的 Release
1. 點擊右側的 **"Releases"** 連結
2. 點擊 **"Create a new release"** 按鈕

### 3. 設定 Release 資訊
- **Tag version**: `v1.1` (應該會自動偵測到已推送的 tag)
- **Release title**: `v1.1 - AI 智能行程規劃功能`
- **Target**: `feature/code-optimization` (或 main branch)

### 4. 填寫 Release 說明
複製以下內容到 Release 描述欄位：

```markdown
## 🎉 桃園景點+ v1.1 正式發布

### 🆕 主要新功能
- **🤖 AI 智能行程規劃**: 整合 Google Gemini AI，提供個人化旅遊行程建議
- **🌍 多語言 AI 支援**: AI 回應支援繁體中文和英文，動態語言同步
- **📱 智能景點推薦**: 根據用戶需求智能推薦桃園景點

### 🛠️ 技術改進
- 啟用 ProGuard/R8 代碼混淆和資源壓縮
- 優化 APK 大小和性能
- 添加完整的混淆規則保護關鍵類別
- 完善文檔和使用說明

### 📊 版本資訊
- **版本號**: 1.1 (Build 2)
- **APK 大小**: 約 7.4 MB
- **最低支援**: Android 7.0 (API 24)
- **目標 SDK**: Android 14 (API 34)

### 📱 安裝說明
1. 下載下方的 `app-release.apk` 檔案
2. 在 Android 裝置上允許未知來源安裝
3. 安裝 APK 並享受 AI 智能行程規劃功能

### 🔧 開發者注意事項
如需使用 AI 功能，請在 `local.properties` 中配置 `GEMINI_API_KEY`

---
**完整更新日誌**: 請參閱 [RELEASE_NOTES_v1.1.md](https://github.com/jongren/TaoyuanTravelApp/blob/feature/code-optimization/RELEASE_NOTES_v1.1.md)
```

### 5. 上傳 APK 檔案
在 **"Attach binaries"** 區域：
1. 點擊 **"choose your files"** 或直接拖拽檔案
2. 上傳以下檔案：
   - `app/build/outputs/apk/release/app-release.apk`

### 6. 發布設定
- ✅ 勾選 **"Set as the latest release"**
- ✅ 勾選 **"Create a discussion for this release"** (可選)

### 7. 發布 Release
點擊 **"Publish release"** 按鈕完成發布

## 📝 發布後檢查清單
- [ ] 確認 Release 頁面顯示正確
- [ ] 測試 APK 下載連結
- [ ] 更新 README.md 中的下載連結 (如需要)
- [ ] 通知用戶新版本發布

## 🔗 相關連結
- **Repository**: https://github.com/jongren/TaoyuanTravelApp
- **Releases**: https://github.com/jongren/TaoyuanTravelApp/releases
- **APK 位置**: `app/build/outputs/apk/release/app-release.apk`