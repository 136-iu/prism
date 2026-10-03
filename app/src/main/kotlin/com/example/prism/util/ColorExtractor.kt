package com.example.prism.util

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.palette.graphics.Palette

/**
 * 从封面图提取主色调，用于动态背景
 */
object ColorExtractor {

    data class Colors(
        val dominant: Int,
        val vibrant: Int,
        val muted: Int,
        val dark: Int
    )

    fun extract(bitmap: Bitmap): Colors {
        val palette = Palette.from(bitmap).generate()
        return Colors(
            dominant = palette.getDominantColor(0xFF7C4DFF.toInt()),
            vibrant = palette.getVibrantColor(0xFF7C4DFF.toInt()),
            muted = palette.getMutedColor(0xFF4A4A6A.toInt()),
            dark = palette.getDarkMutedColor(0xFF1A1A2E.toInt())
        )
    }

    /**
     * 生成从主色到深色的渐变背景三色
     */
    fun buildGradient(bitmap: Bitmap): List<Int> {
        val c = extract(bitmap)
        val vibrant = c.vibrant
        val dark = darken(vibrant, 0.75f)
        val mid = darken(vibrant, 0.45f)
        return listOf(dark, mid, vibrant)
    }

    fun darken(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color, hsv)
        hsv[2] = (hsv[2] * (1f - factor)).coerceIn(0f, 1f)
        return AndroidColor.HSVToColor(hsv)
    }

    fun lighten(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(color, hsv)
        hsv[2] = (hsv[2] + (1f - hsv[2]) * factor).coerceIn(0f, 1f)
        return AndroidColor.HSVToColor(hsv)
    }
}