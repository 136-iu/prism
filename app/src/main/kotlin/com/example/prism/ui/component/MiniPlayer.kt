package com.example.prism.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass

@Composable
fun MiniPlayer(onExpand: () -> Unit, onOpenQueue: () -> Unit) {
    val song by PlayerManager.currentSong.collectAsState()
    val isPlaying by PlayerManager.isPlaying.collectAsState()

    song?.let { s ->
        val accent = ThemeState.accent
        val dark = ThemeState.isDark
        val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f

        var dragY by remember { mutableFloatStateOf(0f) }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(60.dp)
                .graphicsLayer {
                    translationY = dragY.coerceAtMost(0f)
                    alpha = 1f - (-dragY.coerceAtMost(0f) / 200f)
                }
                .liquidGlass(RoundedCornerShape(16.dp), dark,
                    ThemeState.glassTint, gi)
                .pointerInput(s.id) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragY < -60f) onExpand()
                            dragY = 0f
                        },
                        onDrag = { change, drag ->
                            dragY += drag.y
                            change.consume()
                        }
                    )
                }
                .clickable(onClick = onExpand)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                    .background(ThemeState.iconBg),
                contentAlignment = Alignment.Center
            ) {
                if (s.artworkUri != null) {
                    AsyncImage(model = s.artworkUri, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize())
                } else {
                    Text("🎵", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(s.title, color = ThemeState.text,
                    fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(s.artist, color = ThemeState.textDim,
                    fontSize = 11.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }

            Box(
                Modifier.size(36.dp).clip(CircleShape).background(accent)
                    .clickable { PlayerManager.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier.size(36.dp).clip(CircleShape).clickable { PlayerManager.next() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.SkipNext, null,
                    tint = ThemeState.text, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(2.dp))
            Box(
                Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onOpenQueue),
                contentAlignment = Alignment.Center
            ) {
                Text("☰", color = ThemeState.text, fontSize = 16.sp)
            }
        }
    }
}