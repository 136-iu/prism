package com.example.prism.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorPreset(val id: String, val name: String, val value: Long)

object ThemePresets {
    val solids = listOf(
        ColorPreset("purple", "紫", 0xFF7C4DFF),
        ColorPreset("blue", "蓝", 0xFF2196F3),
        ColorPreset("cyan", "青", 0xFF00BCD4),
        ColorPreset("green", "绿", 0xFF4CAF50),
        ColorPreset("orange", "橙", 0xFFFF9800),
        ColorPreset("pink", "粉", 0xFFE91E63)
    )

    fun toColor(value: Long): Color = Color(value)
}