package com.example.prism.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat

/**
 * 各厂商超级岛 / 灵动胶囊实现
 * 目前只搭框架，等注册开发者后接入
 */
object IslandImplementations {

    private const val NOTIFY_ID = 1001

    fun updateMiSuperIsland(
        context: Context, title: String, artist: String, isPlaying: Boolean
    ) {
        // 小米超级岛接入步骤（需注册小米开发者）：
        // 1. 在小米开放平台申请焦点通知权限
        // 2. 发通知时携带 miui.focus.param 扩展参数
        // 3. 参数格式为 JSON：
        //    {"param_v2":{"protocol":1,"business":"prism","id":"xxx"}}
        // 4. 需要真机调试
        //
        // 目前用普通通知占位
        notifyFallback(context, title, artist, isPlaying)
    }

    private fun notifyFallback(
        context: Context, title: String, artist: String, isPlaying: Boolean
    ) {
        try {
            val builder = NotificationCompat.Builder(
                context, MediaNotificationManager.CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(title)
                .setContentText(artist)
                .setOngoing(isPlaying)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true)
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as android.app.NotificationManager
            nm.notify(NOTIFY_ID, builder.build())
        } catch (_: Exception) {}
    }
}