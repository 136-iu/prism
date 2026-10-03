package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.component.SongItem
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass

@Composable
fun HomeScreen(
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onOpenSearch: () -> Unit
) {
    val currentSong by PlayerManager.currentSong.collectAsState()
    val dark = ThemeState.isDark
    val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ★ 顶部：欢迎 + 搜索按钮
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("🎵 欢迎回来", color = ThemeState.text,
                        fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("共 ${allSongs.size} 首歌曲",
                        color = ThemeState.textDim, fontSize = 13.sp)
                }
                // ★ 右上角搜索按钮
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(ThemeState.iconBg)
                        .clickable(onClick = onOpenSearch),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔍", fontSize = 20.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        if (allSongs.isNotEmpty()) {
            item {
                Row(
                    Modifier.fillMaxWidth().height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BigCard(
                        song = allSongs.first(),
                        dark = dark,
                        gi = gi,
                        modifier = Modifier.weight(6f).fillMaxHeight(),
                        onClick = { onSongClick(allSongs.first()) }
                    )
                    Column(
                        Modifier.weight(4f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        allSongs.drop(1).take(2).forEach { s ->
                            SmallCard(
                                song = s,
                                dark = dark,
                                gi = gi,
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                onClick = { onSongClick(s) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            item {
                Text("🎵 更多推荐", color = ThemeState.text,
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
            }

            items(allSongs.drop(3).take(15)) { song ->
                SongItem(
                    song = song,
                    isPlaying = song.id == currentSong?.id,
                    onClick = { onSongClick(song) },
                    showActions = false
                )
            }
        }
    }
}

@Composable
private fun BigCard(
    song: Song,
    dark: Boolean,
    gi: Float,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .liquidGlass(RoundedCornerShape(20.dp), dark, ThemeState.glassTint, gi)
            .clickable(onClick = onClick)
    ) {
        Column(
            Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(song.title, color = ThemeState.text, fontSize = 18.sp,
                fontWeight = FontWeight.Bold, maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(song.artist, color = ThemeState.textDim,
                fontSize = 12.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SmallCard(
    song: Song,
    dark: Boolean,
    gi: Float,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .liquidGlass(RoundedCornerShape(16.dp), dark, ThemeState.glassTint, gi)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(6.dp))
                    .background(ThemeState.iconBg),
                contentAlignment = Alignment.Center
            ) {
                if (song.artworkUri != null) {
                    AsyncImage(model = song.artworkUri, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize())
                } else {
                    Text("🎵", fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(song.title, color = ThemeState.text, fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(song.artist, color = ThemeState.textDim,
                    fontSize = 11.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}