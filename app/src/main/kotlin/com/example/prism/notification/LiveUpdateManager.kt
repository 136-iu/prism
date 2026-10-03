package com.example.prism.notification

import android.app.Notification
import android.os.Build

/**
 * Android 16 的实时活动（Live Updates）
 * 需要 targetSdk 36 才完全生效
 * 目前用普通通知兼容
 */
object LiveUpdateManager {

    fun isSupported(): Boolean = Build.VERSION.SDK_INT >= 35

    fun applyLiveUpdateFlag(builder: Notification.Builder): Notification.Builder {
        if (Build.VERSION.SDK_INT >= 35) {
            try {
                val method = Notification.Builder::class.java
                    .getMethod(
                        "setRequestPromotedOngoing",
                        Boolean::class.javaPrimitiveType
                    )
                method.invoke(builder, true)
            } catch (_: Exception) {}
        }
        return builder
    }
}