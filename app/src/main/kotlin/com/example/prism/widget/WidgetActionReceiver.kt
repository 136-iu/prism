package com.example.prism.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.prism.playback.PlayerManager

class WidgetActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TOGGLE = "com.example.prism.WIDGET_TOGGLE"
        const val ACTION_NEXT = "com.example.prism.WIDGET_NEXT"
        const val ACTION_PREV = "com.example.prism.WIDGET_PREV"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_TOGGLE -> PlayerManager.togglePlayPause()
            ACTION_NEXT -> PlayerManager.next()
            ACTION_PREV -> PlayerManager.previous()
        }
        context?.let { WidgetUpdateHelper.updateAll(it) }
    }
}