package com.example.service11092023

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class MyForegroundService : Service() {

    private val PAUSE_REQUEST_CODE = 0
    private val PLAY_REQUEST_CODE = 1
    private var requestCode = -1
    var progress = 0
    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    inner class MyBinder: Binder() {
        fun getService(): MyForegroundService {
            return this@MyForegroundService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        val notification = makeNotification("Downloading")
        startForeground(1, notification)
        Log.d("pphat", "onCreate")
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestCode = intent?.getIntExtra("requestCode", -1) ?: -1
        CoroutineScope(Dispatchers.IO).launch {
            while (progress <= 100 && requestCode == PLAY_REQUEST_CODE) {
                val notificationProgress = makeNotification("Downloading", progress)
                notificationManager.notify(1, notificationProgress)
                progress += 5
                delay(1000)
            }

            if (progress >= 100) {
                stopSelf()
            }
        }

        Log.d("pphat", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("pphat", "onDestroy")
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
    }

    private fun makeNotification(title: String?, progress: Int? = null): Notification {
        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
        notification.apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setShowWhen(true)
            if (progress != null) {
                setContentInfo("${progress}%")
                setProgress(100, progress, false)
            }
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
//
        val intentPlay = Intent(this@MyForegroundService, MyForegroundService::class.java)
        intentPlay.putExtra("requestCode", PLAY_REQUEST_CODE)
        val pendingPlay = PendingIntent.getService(
            this@MyForegroundService,
            PLAY_REQUEST_CODE,
            intentPlay,
            PendingIntent.FLAG_IMMUTABLE
        )

        val intentPause = Intent(this@MyForegroundService, MyForegroundService::class.java)
        intentPause.putExtra("requestCode", PAUSE_REQUEST_CODE)
        val pendingPause = PendingIntent.getService(
            this@MyForegroundService,
            PAUSE_REQUEST_CODE,
            intentPause,
            PendingIntent.FLAG_IMMUTABLE
        )

        notification.addAction(android.R.drawable.ic_media_play, "Play", pendingPlay)
        notification.addAction(android.R.drawable.ic_media_pause, "Pause", pendingPause)

        return notification.build()
    }

}