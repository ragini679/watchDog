package com.example.watchdog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.*
import android.widget.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.Log

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Root layout
        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setBackgroundColor(Color.parseColor("#EEEEEE"))
        rootLayout.gravity = Gravity.CENTER

        // Card layout
        val cardLayout = LinearLayout(this)
        cardLayout.orientation = LinearLayout.VERTICAL
        cardLayout.setPadding(60,60,60,60)
        cardLayout.setBackgroundColor(Color.WHITE)
        cardLayout.gravity = Gravity.CENTER

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Message Text
        val text = TextView(this)
        text.text = "⚠️ App Detected\nTake a breath."
        text.textSize = 22f
        text.setTextColor(Color.BLACK)
        text.gravity = Gravity.CENTER

        val textParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.bottomMargin = 40

        // Button
        val button = Button(this)
        button.text = "Dismiss"

        button.setOnClickListener {

            Log.d("WATCHDOG","Overlay dismissed")

            windowManager.removeView(overlayView)

            stopSelf()
        }

        cardLayout.addView(text, textParams)
        cardLayout.addView(button)

        rootLayout.addView(cardLayout, cardParams)

        overlayView = rootLayout

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}