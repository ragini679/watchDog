package com.example.watchdog

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class WatchdogService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var monitoredPackage: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        monitoredPackage = intent?.getStringExtra("package")?.trim()?.lowercase() ?: ""

        Log.d("WATCHDOG","Monitoring package: $monitoredPackage")
        startForeground(1, createNotification())

        handler.post(checkRunnable)

        return START_STICKY
    }

    private val checkRunnable = object : Runnable {

        override fun run() {

            val currentApp = getForegroundApp()

            Log.d("WATCHDOG","Current app: $currentApp")

            if (currentApp == monitoredPackage) {

                Log.d("WATCHDOG","Target app detected")

                val intent = Intent(this@WatchdogService, OverlayService::class.java)
                startService(intent)

            }

            handler.postDelayed(this, 5000)

        }

    }

    private fun getForegroundApp(): String {

        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val time = System.currentTimeMillis()

        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 10000,
            time
        )

        if (stats != null) {

            val sorted = stats.sortedByDescending { it.lastTimeUsed }

            if (sorted.isNotEmpty()) {

                return sorted[0].packageName.lowercase()

            }

        }

        return ""

    }

    private fun createNotification(): Notification {

        val channelId = "watchdog_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Watchdog Service",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)

        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Watchdog Running")
            .setContentText("Monitoring apps")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}