package com.example.taoyuantravel

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 自訂的 Application 類別
 *
 * @HiltAndroidApp 這個註解會觸發 Hilt 的程式碼生成，
 * 包含一個應用程式層級的依賴容器。
 * 這個註解是使用 Hilt 的必要起點。
 */
@HiltAndroidApp
class TaoyuanTravelApp : Application() {
    // 目前不需要在這裡添加任何額外的程式碼
    // Hilt 會在背景自動處理所有初始化工作
}