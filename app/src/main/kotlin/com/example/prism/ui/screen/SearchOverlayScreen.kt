package com.example.prism.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Song
import com.example.prism.ui.theme.ThemeState

@Composable
fun SearchOverlayScreen(
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onClose: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(380)) }

    Box(
        Modifier.fillMaxSize()
            .graphicsLayer {
                scaleX = 0.6f + progress.value * 0.4f
                scaleY = 0.6f + progress.value * 0.4f
                alpha = progress.value
            }
            .background(
                Brush.verticalGradient(
                    if (ThemeState.isDark)
                        listOf(Color(0xFF0F0F14), Color(0xFF1A1A2E))
                    else
                        listOf(Color(0xFFF5F5F8), Color(0xFFEDEDF2))
                )
            )
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(
                            if (ThemeState.isDark) Color.White.copy(alpha = 0.1f)
                            else Color.Black.copy(alpha = 0.06f)
                        )
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←",
                        color = if (ThemeState.isDark) Color.White else Color.Black,
                        fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text("搜索",
                    color = if (ThemeState.isDark) Color.White else Color.Black,
                    fontSize = 20.sp)
            }
            Box(Modifier.weight(1f)) {
                SearchScreen(allSongs = allSongs, onSongClick = onSongClick)
            }
        }
    }
}