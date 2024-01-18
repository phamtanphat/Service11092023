package com.example.service11092023

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var btnEnableForegroundService: Button
    private lateinit var btnDisableForegroundService: Button
    private lateinit var progressBar: ProgressBar
    private var progress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEnableForegroundService = findViewById(R.id.button_enable_foreground)
        btnDisableForegroundService = findViewById(R.id.button_disable_foreground)
        progressBar = findViewById(R.id.progress_bar)

        btnEnableForegroundService.setOnClickListener {
            val intent = Intent(this@MainActivity, MyForegroundService::class.java)
            ContextCompat.startForegroundService(this@MainActivity, intent)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isMyServiceRunning(MyForegroundService::class.java)) {
            val intent = Intent(this@MainActivity, MyForegroundService::class.java)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val myService = (binder as MyForegroundService.MyBinder).getService()
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    progress = myService.progress
                    progressBar.progress = progress
                    if (progress < 100) {
                        Handler(Looper.getMainLooper()).postDelayed(this, 1000)
                    }
                }
            }, 1000)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }

    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}