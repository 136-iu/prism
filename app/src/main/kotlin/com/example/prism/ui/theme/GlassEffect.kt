package com.example.prism.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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