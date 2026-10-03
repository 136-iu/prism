package com.example.prism.ui.screen

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.playback.PlayerManager
import com.example.prism.service.DesktopLyricsService
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemeState

@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val song by PlayerManager.currentSong.collectAsState()
    val isPlaying by PlayerManager.isPlaying.collectAsState()
    val accent = ThemeState.accent
    val style = ThemeState.playerStyle
    val context = LocalContext.current

    song?.let { s ->
        when (style) {
            PlayerStyle.SALT -> SaltPlayerStyle(
                song = s, isPlaying = isPlaying, accent = accent,
                onBack = onBack, onOpenLyrics = onOpenLyrics,
                onOpenQueue = onOpenQueue, onOpenEqualizer = onOpenEqualizer
            )
            PlayerStyle.IOS26 -> IOS26PlayerStyle(
                song = s, isPlaying = isPlaying, accent = accent,
                onBack = onBack, onOpenLyrics = onOpenLyrics,
                onOpenQueue = onOpenQueue, onOpenEqualizer = onOpenEqualizer
            )
        }
    }
}

// ---------- iOS 26 风格 ----------
@Composable
private fun IOS26PlayerStyle(
    song: com.example.prism.data.Song,
    isPlaying: Boolean,
    accent: Color,
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (song.artworkUri != null) {
            AsyncImage(
                model = song.artworkUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.35f }
            )
        }
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color.Black.copy(alpha = 0.6f), Color.Black.copy(alpha = 0.9f))
            )
        ))

        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, "返回", tint = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable(onClick = onOpenQueue)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("📋 队列", color = Color.White, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.weight(0.5f))

            Box(
                Modifier.fillMaxWidth(0.8f).aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (song.artworkUri != null) {
                    AsyncImage(model = song.artworkUri, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize())
                } else {
                    Text("🎵", fontSize = 80.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(song.title, color = Color.White, fontSize = 26.sp,
                        fontWeight = FontWeight.Bold, maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(song.artist, color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier.size(56.dp).clip(CircleShape).background(accent)
                        .clickable { PlayerManager.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        "播放/暂停", tint = Color.White,
                        modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable { PlayerManager.previous() },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipPrevious, "上一首",
                        tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable(onClick = onOpenLyrics),
                    contentAlignment = Alignment.Center) {
                    Text("📝", fontSize = 22.sp)
                }
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable(onClick = onOpenEqualizer),
                    contentAlignment = Alignment.Center) {
                    Text("🎚", fontSize = 22.sp)
                }
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable { PlayerManager.next() },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipNext, "下一首",
                        tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ---------- Salt Player 风格 ----------
@Composable
private fun SaltPlayerStyle(
    song: com.example.prism.data.Song,
    isPlaying: Boolean,
    accent: Color,
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = Color.White)
            }
        }

        Spacer(Modifier.weight(0.4f))

        Box(
            Modifier.fillMaxWidth(0.75f).aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (song.artworkUri != null) {
                AsyncImage(model = song.artworkUri, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize())
            } else {
                Text("🎵", fontSize = 80.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(song.title, color = Color.White, fontSize = 20.sp,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(song.artist, color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(56.dp).clip(CircleShape)
                .clickable { PlayerManager.previous() },
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SkipPrevious, "上一首",
                    tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Box(Modifier.size(72.dp).clip(CircleShape).background(accent)
                .clickable { PlayerManager.togglePlayPause() },
                contentAlignment = Alignment.Center) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    "播放/暂停", tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Box(Modifier.size(56.dp).clip(CircleShape)
                .clickable { PlayerManager.next() },
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SkipNext, "下一首",
                    tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToolIcon("🔁") { PlayerManager.cycleRepeatMode() }
            ToolIcon("⏱") { com.example.prism.playback.SleepTimer.start(30) }
            ToolIcon("🎚", onClick = onOpenEqualizer)
            ToolIcon("☰", onClick = onOpenQueue)
            ToolIcon("📝", onClick = onOpenLyrics)
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ToolIcon(emoji: String, onClick: () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 20.sp)
    }
}