package com.example.prism.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.data.Song

@Composable
fun SongMenuSheet(
    song: Song,
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onNextPlay: () -> Unit,
    onShare: () -> Unit,
    onArtist: () -> Unit,
    onAlbum: () -> Unit,
    onEditTags: () -> Unit,
    onSpectrum: () -> Unit,
    onSongInfo: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.60f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF1A1A1E))
                .clickable(enabled = false) {}
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            // 歌曲信息行
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
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
                    Spacer(Modifier.height(2.dp))
                    Text(song.artist, color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
                Text("🎵", fontSize = 20.sp, color = Color(0xFF4A9EFF))
            }

            Spacer(Modifier.height(8.dp))

            MenuRow("＋", "添加到歌单", Color(0xFF4A9EFF), onAddToPlaylist)
            MenuRow("⏭", "下一首播放", Color(0xFF4A9EFF), onNextPlay)
            MenuRow("📤", "分享", Color(0xFF4A9EFF), onShare)
            MenuRow("🎤", "艺术家：${song.artist}", Color(0xFFFFB74D), onArtist)
            MenuRow("💿", "专辑：${song.album}", Color(0xFFEF5350), onAlbum)
            MenuRow("✏️", "编辑歌曲信息", Color(0xFF66BB6A), onEditTags)
            MenuRow("🔥", "频谱可视化", Color(0xFF8D6E63), onSpectrum)
            MenuRow("ℹ️", "歌曲信息", Color(0xFF90A4AE), onSongInfo)
            MenuRow("🗑️", "永久删除", Color(0xFFFF5252), onDelete)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MenuRow(
    emoji: String,
    text: String,
    emojiColor: Color,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(16.dp))
        Text(
            text,
            color = if (emoji == "🗑️") Color(0xFFFF5252) else Color.White,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}