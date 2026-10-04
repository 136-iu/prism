package com.example.prism.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.liquidGlass(
    shape: Shape,
    dark: Boolean,
    tint: Color = Color.White.copy(alpha = 0.10f),
    intensity: Float = 0.55f
): Modifier {
    val i = intensity.coerceIn(0f, 1f)
    val tintA = (1f - i) * 0.6f + 0.05f
    return this
        .clip(shape)
        .background(
            Brush.linearGradient(
                listOf(
                    tint.copy(alpha = tintA * 1.4f),
                    tint.copy(alpha = tintA * 0.6f)
                )
            )
        )
        .border(
            width = (0.8f + i * 0.6f).dp,
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = i * (if (dark) 0.4f else 0.9f)),
                    Color.White.copy(alpha = i * (if (dark) 0.05f else 0.2f))
                )
            ),
            shape = shape
        )
}

/**
 * 全局玻璃背景：渐变 + 主色光晕 + 顶部高光
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val accent = ThemeState.accent
    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ThemeState.bgStart, ThemeState.bgMid, ThemeState.bgEnd)
                )
            )
            // 主色光晕（底部偏上，营造氛围感）
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.18f),
                        accent.copy(alpha = 0f)
                    ),
                    center = Offset(0.5f, 0.35f),
                    radius = 1200f
                )
            )
            // 顶部高光
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    endY = 400f
                )
            )
    ) {
        content()
    }
}
