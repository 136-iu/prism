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
import androidx.compose.ui.draw.blur
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
import com.example.prism.data.Song
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemeState
import com.example.prism.util.LyricsParser
import com.example.prism.util.LyricsStore
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val song by PlayerManager.currentSong.collectAsState()
    val isPlaying by PlayerManager.isPlaying.collectAsState()
    val progress by PlayerManager.progressMs.collectAsState()
    val duration by PlayerManager.durationMs.collectAsState()
    val accent = ThemeState.accent
    val style = ThemeState.playerStyle

   
    song?.let { s ->
        when (style) {
            PlayerStyle.SALT -> SaltStyle(
                song = s, isPlaying = isPlaying,
                progress = progress, duration = duration,
                accent = accent,
                onBack = onBack, onOpenLyrics = onOpenLyrics,
                onOpenQueue = onOpenQueue, onOpenEqualizer = onOpenEqualizer
            )
            PlayerStyle.IOS26 -> IOS26Style(
                song = s, isPlaying = isPlaying,
                progress = progress, duration = duration,
                accent = accent,
                onBack = onBack, onOpenLyrics = onOpenLyrics,
                onOpenQueue = onOpenQueue, onOpenEqualizer = onOpenEqualizer
            )
        }
    }
}

// ==================== iOS 26 风格 ====================
@Composable
private fun IOS26Style(
    song: Song, isPlaying: Boolean,
    progress: Long, duration: Long,
    accent: Color,
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val context = LocalContext.current
    val lyricsPreview = remember(song.id) {
        val text = LyricsStore.load(context, song) ?: ""
        val lines = LyricsParser.parse(text)
        lines.take(2).map { it.text }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // 封面模糊背景
        if (song.artworkUri != null) {
            AsyncImage(
                model = song.artworkUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.4f }.blur(60.dp)
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.9f))
                )
            )
        )

        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部
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

            Spacer(Modifier.weight(0.4f))

            // 封面
            Box(
                Modifier.fillMaxWidth(0.78f).aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (song.artworkUri != null) {
                    AsyncImage(
                        model = song.artworkUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("🎵", fontSize = 80.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // 歌名 + 右侧播放按钮
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(song.title, color = Color.White, fontSize = 24.sp,
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
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 进度条
            ProgressBar(progress = progress, duration = duration, accent = accent)

            Spacer(Modifier.height(20.dp))

            // 控制
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

            Spacer(Modifier.height(16.dp))

            // ★ 歌词预览 2 行（在控制下面）
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(onClick = onOpenLyrics)
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    if (lyricsPreview.isEmpty()) {
                        Text("📝 点击查看歌词",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp)
                    } else {
                        lyricsPreview.forEachIndexed { i, line ->
                            Text(line,
                                color = if (i == 0) Color.White else Color.White.copy(alpha = 0.5f),
                                fontSize = if (i == 0) 14.sp else 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                            if (i == 0) Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(0.5f))
        }
    }
}

// ==================== Salt Player 风格 ====================
@Composable
private fun SaltStyle(
    song: Song, isPlaying: Boolean,
    progress: Long, duration: Long,
    accent: Color,
    onBack: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    val context = LocalContext.current
    val lyricsPreview = remember(song.id) {
        val text = LyricsStore.load(context, song) ?: ""
        val lines = LyricsParser.parse(text)
        lines.take(2).map { it.text }
    }

    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape)
                    .background(ThemeState.iconBg)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = ThemeState.text)
            }
        }

        Spacer(Modifier.weight(0.4f))

        Box(
            Modifier.fillMaxWidth(0.72f).aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(ThemeState.iconBg),
            contentAlignment = Alignment.Center
        ) {
            if (song.artworkUri != null) {
                AsyncImage(
                    model = song.artworkUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("🎵", fontSize = 80.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(song.title, color = ThemeState.text, fontSize = 18.sp,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(song.artist, color = ThemeState.textDim,
            fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

        Spacer(Modifier.height(16.dp))

        // 进度条
        ProgressBar(progress = progress, duration = duration, accent = accent)

        Spacer(Modifier.weight(1f))

        // 主控制
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(56.dp).clip(CircleShape)
                .clickable { PlayerManager.previous() },
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SkipPrevious, "上一首",
                    tint = ThemeState.text, modifier = Modifier.size(32.dp))
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
                    tint = ThemeState.text, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // 底部 5 图标
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ToolIcon("🔁") { PlayerManager.cycleRepeatMode() }
            ToolIcon("⏱") { com.example.prism.playback.SleepTimer.start(30) }
            ToolIcon("🎚", onClick = onOpenEqualizer)
            ToolIcon("☰", onClick = onOpenQueue)
            ToolIcon("📝", onClick = onOpenLyrics)
        }

        Spacer(Modifier.height(14.dp))

        // 歌词预览
        Box(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ThemeState.cardBg)
                .clickable(onClick = onOpenLyrics)
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                if (lyricsPreview.isEmpty()) {
                    Text("📝 点击查看歌词",
                        color = ThemeState.textFaint, fontSize = 13.sp)
                } else {
                    lyricsPreview.forEachIndexed { i, line ->
                        Text(line,
                            color = if (i == 0) ThemeState.text else ThemeState.textDim,
                            fontSize = if (i == 0) 14.sp else 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                        if (i == 0) Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ProgressBar(progress: Long, duration: Long, accent: Color) {
    val ratio = if (duration > 0) (progress.toFloat() / duration).coerceIn(0f, 1f) else 0f

    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier.fillMaxWidth().height(4.dp)
                .clip(CircleShape)
                .background(ThemeState.textFaint)
        ) {
            Box(
                Modifier.fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .background(accent)
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(fmt(progress), color = ThemeState.textDim, fontSize = 11.sp)
            Text(fmt(duration), color = ThemeState.textDim, fontSize = 11.sp)
        }
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

private fun fmt(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}