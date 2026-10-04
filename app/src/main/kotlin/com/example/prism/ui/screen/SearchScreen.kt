package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Song
import com.example.prism.data.Storage
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.component.SongItem
import com.example.prism.ui.theme.ThemeState
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf(Storage.getSearchScope()) }
    var results by remember { mutableStateOf<List<Song>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    val currentSong by PlayerManager.currentSong.collectAsState()
    val accent = ThemeState.accent

    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
            return@LaunchedEffect
        }
        searching = true
        delay(300)
        results = allSongs.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true)
        }
        searching = false
    }

    Column(Modifier.fillMaxSize()) {
        Text("🔍 搜索", color = ThemeState.text, fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp))

        // 搜索框
        Box(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(ThemeState.cardBg)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text("🔍", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("搜索歌曲、歌手、专辑",
                            color = ThemeState.textFaint, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = ThemeState.text, fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(accent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // 范围
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("全部", "本地", "在线").forEach { s ->
                val sel = s == scope
                Box(
                    Modifier.clip(CircleShape)
                        .background(if (sel) accent.copy(alpha = 0.3f)
                                    else ThemeState.cardBg)
                        .clickable {
                            scope = s
                            Storage.setSearchScope(s)
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(s,
                        color = if (sel) ThemeState.text else ThemeState.textDim,
                        fontSize = 12.sp,
                        fontWeight = if (sel) FontWeight.SemiBold
                                     else FontWeight.Normal)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        when {
            query.isBlank() -> Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text("输入关键词开始搜索",
                    color = ThemeState.textFaint, fontSize = 14.sp)
            }
            searching -> Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text("搜索中…", color = ThemeState.textFaint, fontSize = 14.sp)
            }
            results.isEmpty() -> Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text("没有找到 \"$query\"",
                    color = ThemeState.textFaint, fontSize = 14.sp)
            }
            else -> LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(results) { song ->
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
}
