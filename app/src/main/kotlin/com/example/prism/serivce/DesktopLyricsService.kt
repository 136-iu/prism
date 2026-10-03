package com.example.prism.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.example.prism.playback.PlayerManager

/**
 * 桌面歌词悬浮窗
 * 需要 SYSTEM_ALERT_WINDOW 权限
 */
class DesktopLyricsService : Service() {

    private var windowManager: WindowManager? = null
    private var lyricsView: TextView? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createView()
        startUpdateLoop()
    }

    private fun createView() {
        lyricsView = TextView(this).apply {
            text = "桌面歌词"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setPadding(30, 20, 30, 20)
            setBackgroundColor(0xCC1A1A2E.toInt())
        }

        val type = if (Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 200
        }

        windowManager?.addView(lyricsView, params)
    }

    private fun startUpdateLoop() {
        // 简单轮询，每 500ms 更新一次
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                updateLyrics()
                handler.postDelayed(this, 500)
            }
        }
        handler.post(runnable)
    }

    private fun updateLyrics() {
        val song = PlayerManager.currentSong.value ?: return
        lyricsView?.text = song.title
    }

    override fun onDestroy() {
        try {
            lyricsView?.let { windowManager?.removeView(it) }
        } catch (_: Exception) {}
        lyricsView = null
        super.onDestroy()
    }
}