package com.example.prism.notification

import android.content.Context
import android.os.Build

/**
 * 系统岛分发器
 * 各厂商岛适配需要注册开发者账号
 * 当前只保留框架，后续接入
 */
object IslandDispatcher {

    enum class SystemType {
        MIUI, VIVO, OPPO, HONOR, HUAWEI, SAMSUNG, UNKNOWN
    }

    fun detectSystem(): SystemType {
        val brand = Build.BRAND.lowercase()
        return when {
            brand.contains("xiaomi") || brand.contains("redmi") -> SystemType.MIUI
            brand.contains("vivo") || brand.contains("iqoo") -> SystemType.VIVO
            brand.contains("oppo") || brand.contains("realme") -> SystemType.OPPO
            brand.contains("honor") -> SystemType.HONOR
            brand.contains("huawei") -> SystemType.HUAWEI
            brand.contains("samsung") -> SystemType.SAMSUNG
            else -> SystemType.UNKNOWN
        }
    }

    fun update(context: Context, title: String, artist: String, isPlaying: Boolean) {
        // 各厂商岛适配延后
        when (detectSystem()) {
            SystemType.MIUI -> IslandImplementations.updateMiSuperIsland(
                context, title, artist, isPlaying
            )
            else -> {}
        }
    }
}