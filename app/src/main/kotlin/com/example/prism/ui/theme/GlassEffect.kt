package com.example.prism.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================================
// 核心：液态玻璃修饰符
// ============================================================
fun Modifier.liquidGlass(
    shape: Shape,
    dark: Boolean,
    tint: Color = Color.Unspecified,
    intensity: Float = 0.6f,
    highLight: Boolean = true
): Modifier {
    val i = intensity.coerceIn(0f, 1f)

    // 玻璃基色
    val baseTint = if (tint == Color.Unspecified) {
        if (dark) Color(0xFF252540) else Color(0xFFFFFFFF)
    } else tint

    // 透明度映射：intensity 越高越透明
    val fillTopAlpha = if (dark) (0.32f - i * 0.18f) else (0.60f - i * 0.30f)
    val fillBottomAlpha = if (dark) (0.16f - i * 0.08f) else (0.35f - i * 0.18f)
    val borderAlpha = if (dark) (0.18f + i * 0.30f) else (0.55f + i * 0.35f)

    var modifier = this
        .clip(shape)
        // 主渐变底
        .background(
            Brush.linearGradient(
                colors = listOf(
                    baseTint.copy(alpha = fillTopAlpha.coerceAtLeast(0.03f)),
                    baseTint.copy(alpha = fillBottomAlpha.coerceAtLeast(0.02f))
                )
            )
        )

    // 左上角内高光（模拟光照）
    if (highLight) {
        modifier = modifier.background(
            Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (dark) 0.12f * i else 0.25f * i),
                    Color.Transparent
                ),
                center = Offset(100f, 100f),
                radius = 500f
            )
        )
    }

    // 高光边框
    return modifier.border(
        width = (0.8f + i * 0.6f).dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = borderAlpha * 0.12f),
                Color.White.copy(alpha = borderAlpha * 0.45f)
            )
        ),
        shape = shape
    )
}

// ============================================================
// 全局玻璃背景（最外层包一层）
// ============================================================
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dark = ThemeState.isDark
    val bgStart = ThemeState.bgStart
    val bgMid = ThemeState.bgMid
    val bgEnd = ThemeState.bgEnd
    val accent = ThemeState.accent

    Box(
        modifier.background(
            Brush.verticalGradient(listOf(bgStart, bgMid, bgEnd))
        )
    ) {
        // 主色光晕（左上）
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (dark) 0.20f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(300f, 300f),
                        radius = 1400f
                    )
                )
        )
        // 副光晕（右下）
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2196F3).copy(alpha = if (dark) 0.12f else 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(1200f, 2000f),
                        radius = 1800f
                    )
                )
        )
        // 顶部高光
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (dark) 0.05f else 0.20f),
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

// ============================================================
// 通用玻璃卡片组件
// ============================================================
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    tint: Color = Color.Unspecified,
    intensity: Float = -1f,
    padding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val dark = ThemeState.isDark
    val gi = if (intensity < 0f) {
        if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f
    } else intensity

    Box(
        modifier.liquidGlass(
            shape = shape,
            dark = dark,
            tint = if (tint == Color.Unspecified) ThemeState.glassTint else tint,
            intensity = gi
        )
    ) {
        Box(Modifier.padding(padding)) {
            content()
        }
    }
}