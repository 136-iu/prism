package com.example.prism.ui.screen

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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.data.Song
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.component.LyricsView
import com.example.prism.ui.theme.ThemeState
import com.example.prism.util.LyricsParser
import com.example.prism.util.LyricsStore
import kotlinx.coroutines.delay

@Composable
fun LyricsScreen(
    song: Song,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isPlaying by PlayerManager.isPlaying.collectAsState()
    val progress by PlayerManager.progressMs.collectAsState()
    val accent = ThemeState.accent

    var lyricsText by remember(song.id) {
        mutableStateOf(LyricsStore.load(context, song) ?: "")
    }
    val lyrics = remember(lyricsText) { LyricsParser.parse(lyricsText) }
    val currentIndex = remember(lyrics, progress) {
        LyricsParser.findCurrentIndex(lyrics, progress)
    }
    val inLineProgress = remember(lyrics, currentIndex, progress) {
        LyricsParser.progressInLine(lyrics, currentIndex, progress)
    }

    // 进度轮询
    LaunchedEffect(song.id) {
        while (true) {
            PlayerManager.updateProgress()
            delay(200)
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // 封面模糊背景
        if (song.artworkUri != null) {
            AsyncImage(
                model = song.artworkUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(60.dp)
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.65f),
                        Color.Black.copy(alpha = 0.88f)
                    )
                )
            )
        )

        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            // 顶部：封面 + 歌名歌手 + ⋮
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, "返回",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Spacer(Modifier.width(12.dp))

                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (song.artworkUri != null) {
                        AsyncImage(model = song.artworkUri, contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize())
                    } else {
                        Text("🎵", fontSize = 20.sp)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(song.title, color = Color.White,
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }

                Box(
                    Modifier.size(36.dp).clip(CircleShape)
                        .clickable { /* 更多菜单 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⋮", color = Color.White, fontSize = 20.sp)
                }
            }

            // 歌词区
            Box(Modifier.weight(1f)) {
                if (lyrics.isEmpty()) {
                    EmptyLyrics(song, accent, context, onLyricsLoaded = { lyricsText = it })
                } else {
                    LyricsView(
                        lines = lyrics,
                        currentIndex = currentIndex,
                        inLineProgress = inLineProgress,
                        onLineClick = { PlayerManager.seekTo(it.timeMs) },
                        onLineLongClick = { /* 复制到剪贴板 */ }
                    )
                }
            }

            // 底部：进度条 + 时间
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                val duration = PlayerManager.durationMs.collectAsState().value
                val progressRatio = if (duration > 0) progress.toFloat() / duration else 0f

                Box(
                    Modifier.fillMaxWidth().height(2.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                ) {
                    Box(
                        Modifier.fillMaxWidth(progressRatio)
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(fmt(progress), color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(fmt(duration), color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 三控制
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable { PlayerManager.previous() },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipPrevious, "上一首",
                        tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Box(Modifier.size(60.dp).clip(CircleShape)
                    .clickable { PlayerManager.togglePlayPause() },
                    contentAlignment = Alignment.Center) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        "播放/暂停", tint = Color.White,
                        modifier = Modifier.size(32.dp))
                }
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable { PlayerManager.next() },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipNext, "下一首",
                        tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // 底部三工具
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(Modifier.size(40.dp).clip(CircleShape)
                    .clickable { onBack() },
                    contentAlignment = Alignment.Center) {
                    Text("🎤", fontSize = 18.sp)
                }
                Box(Modifier.size(40.dp).clip(CircleShape)
                    .clickable { /* 队列 */ },
                    contentAlignment = Alignment.Center) {
                    Text("📋", fontSize = 18.sp)
                }
                Box(Modifier.size(40.dp).clip(CircleShape)
                    .clickable { /* 翻译 */ },
                    contentAlignment = Alignment.Center) {
                    Text("💬", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EmptyLyrics(
    song: Song,
    accent: Color,
    context: android.content.Context,
    onLyricsLoaded: (String) -> Unit
) {
    var loading by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎵", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("暂无歌词", color = Color.White.copy(alpha = 0.6f), fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.30f))
                    .clickable {
                        loading = true
                        // 触发联网搜索（后续接入 LyricsFetcher）
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    if (loading) "搜索中…" else "🔍 搜索歌词",
                    color = Color.White, fontSize = 13.sp
                )
            }
        }
    }
}

private fun fmt(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}