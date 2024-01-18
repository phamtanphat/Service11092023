package com.example.service11092023

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication: Application() {

    companion object {
        val CHANNEL_ID = "channel_1"
    }

    override fun onCreate() {
        super.onCreate()
        val notificationManager: NotificationManager by lazy {
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChanel = NotificationChannel(
                CHANNEL_ID,
                "Cập nhật",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(notificationChanel)
        }
    }
}