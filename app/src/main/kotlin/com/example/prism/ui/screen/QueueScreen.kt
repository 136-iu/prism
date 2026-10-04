package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.theme.ThemeState

@Composable
fun QueueScreen(onBack: () -> Unit) {
    val playlist by PlayerManager.playlist.collectAsState()
    val current by PlayerManager.currentSong.collectAsState()
    val repeatMode by PlayerManager.repeatMode.collectAsState()
    val accent = ThemeState.accent

    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ThemeState.bgStart, ThemeState.bgMid, ThemeState.bgEnd)
                )
            )
            .pointerInput(Unit) {
                // 下滑返回
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 80f) onBack()
                }
            }
    ) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            // 顶部提示
            Text(
                "此处向下轻扫以返回播放界面",
                color = ThemeState.textFaint,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 当前播放卡片
            current?.let { song ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeState.cardBg)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
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
                            Text("🎵", fontSize = 20.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            song.title,
                            color = ThemeState.text,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            song.artist,
                            color = ThemeState.textDim,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 计数 + 清除
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentIdx = playlist.indexOfFirst { it.id == current?.id }
                Text(
                    "${if (currentIdx >= 0) currentIdx + 1 else 0} / ${playlist.size}",
                    color = ThemeState.textFaint,
                    fontSize = 13.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "播放队列",
                    color = ThemeState.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "清除",
                    color = ThemeState.textDim,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { showClearDialog = true }
                        .padding(4.dp)
                )
            }

            // 列表
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                itemsIndexed(playlist) { index, song ->
                    val isCurrent = song.id == current?.id
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isCurrent) ThemeState.cardBgStrong
                                else Color.Transparent
                            )
                            .clickable {
                                PlayerManager.playSong(song, playlist)
                            }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                song.title,
                                color = if (isCurrent) accent else ThemeState.text,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                song.artist,
                                color = ThemeState.textDim,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    // 从队列移除
                                    val newList = playlist.toMutableList()
                                    newList.removeAt(index)
                                    if (newList.isEmpty()) return@clickable
                                    val nowCurrent = PlayerManager.currentSong.value
                                    if (nowCurrent != null) {
                                        PlayerManager.playSong(nowCurrent, newList)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "—",
                                color = ThemeState.textFaint,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // 底部循环模式按钮
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 12.dp)
                    .clip(CircleShape)
                    .background(ThemeState.cardBg)
                    .clickable { PlayerManager.cycleRepeatMode() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (repeatMode) {
                        0 -> "列表顺序播放"
                        1 -> "列表循环播放模式"
                        2 -> "单曲循环播放模式"
                        else -> "列表循环播放模式"
                    },
                    color = ThemeState.text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清空播放队列？", color = ThemeState.text) },
            text = {
                Text(
                    "当前播放的歌曲会保留，其他清空。",
                    color = ThemeState.textDim
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // 保留当前歌，其他清空
                    val now = PlayerManager.currentSong.value
                    if (now != null) {
                        PlayerManager.playSong(now, listOf(now))
                    }
                    showClearDialog = false
                }) {
                    Text("确定", color = accent, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消", color = ThemeState.textDim)
                }
            },
            containerColor = ThemeState.bgMid
        )
    }
}