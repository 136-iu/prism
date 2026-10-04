package com.example.prism.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 液态玻璃修饰符（升级版）
 * 效果包含：半透明底 + 高光边 + 渐变 + 内高光
 */
fun Modifier.liquidGlass(
    shape: Shape,
    dark: Boolean,
    tint: Color = Color.Unspecified,
    intensity: Float = 0.6f,
    highLight: Boolean = true
): Modifier {
    val i = intensity.coerceIn(0f, 1f)

    // 玻璃基色：深浅色不同
    val baseTint = if (tint == Color.Unspecified) {
        if (dark) Color(0xFF1E1E32) else Color(0xFFFFFFFF)
    } else tint

    // 透明度：intensity 越高越透明（越"玻璃"）
    val fillTopAlpha = if (dark) (0.28f - i * 0.15f) else (0.55f - i * 0.25f)
    val fillBottomAlpha = if (dark) (0.14f - i * 0.08f) else (0.32f - i * 0.15f)
    val borderAlpha = if (dark) (0.15f + i * 0.25f) else (0.5f + i * 0.3f)

    return this
        .clip(shape)
        // 主渐变底
        .background(
            Brush.linearGradient(
                colors = listOf(
                    baseTint.copy(alpha = fillTopAlpha.coerceAtLeast(0.05f)),
                    baseTint.copy(alpha = fillBottomAlpha.coerceAtLeast(0.02f))
                )
            )
        )
        // 左上角内高光（模拟光照）
        .background(
            Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (dark) 0.10f * i else 0.20f * i),
                    Color.Transparent
                ),
                radius = 400f
            )
        )
        // 高光边框
        .border(
            width = (0.8f + i * 0.5f).dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = borderAlpha),
                    Color.White.copy(alpha = borderAlpha * 0.15f),
                    Color.White.copy(alpha = borderAlpha * 0.4f)
                )
            ),
            shape = shape
        )
}

/**
 * 全局玻璃背景
 * 在 App 最外层包一层，所有内容都透出这层玻璃感
 */
@androidx.compose.runtime.Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    val dark = ThemeState.isDark
    val bgStart = ThemeState.bgStart
    val bgMid = ThemeState.bgMid
    val bgEnd = ThemeState.bgEnd

    androidx.compose.foundation.layout.Box(
        modifier.background(
            Brush.verticalGradient(listOf(bgStart, bgMid, bgEnd))
        )
    ) {
        // ★ 叠加一层大范围柔和玻璃光
        androidx.compose.foundation.layout.Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ThemeState.accent.copy(alpha = if (dark) 0.18f else 0.10f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )
        // ★ 再叠一层顶部高光
        androidx.compose.foundation.layout.Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (dark) 0.04f else 0.15f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}