package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Song
import com.example.prism.data.Storage
import com.example.prism.playback.PlayerManager
import com.example.prism.ui.component.SongItem
import com.example.prism.ui.theme.ThemeState
import com.example.prism.util.PinyinHelper
import kotlinx.coroutines.launch

enum class LibraryTab(val label: String) {
    SONGS("歌曲"), ALBUMS("专辑"), ARTISTS("歌手"), FOLDERS("文件夹")
}

@Composable
fun LibraryScreen(
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onOpenPlaylists: () -> Unit
) {
    var tab by remember { mutableStateOf(LibraryTab.SONGS) }
    val currentSong by PlayerManager.currentSong.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 读设置
    val showFolder = Storage.getLibShowFolder()
    val showAlbum = Storage.getLibShowAlbum()
    val showArtist = Storage.getLibShowArtist()

    val grouped = remember(allSongs) { PinyinHelper.groupByLetter(allSongs) }
    val sortedLetters = remember(grouped) { grouped.keys.toList() }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📚 资料库", color = ThemeState.text, fontSize = 24.sp,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.clip(CircleShape)
                    .background(ThemeState.iconBg)
                    .clickable(onClick = onOpenPlaylists)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("📁 歌单", color = ThemeState.text, fontSize = 13.sp)
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibraryTab.values().forEach { t ->
                // 根据设置过滤 tab
                val visible = when (t) {
                    LibraryTab.ALBUMS -> showAlbum
                    LibraryTab.ARTISTS -> showArtist
                    LibraryTab.FOLDERS -> showFolder
                    else -> true
                }
                if (!visible) return@forEach

                val sel = t == tab
                Box(
                    Modifier.weight(1f).clip(CircleShape)
                        .background(if (sel) ThemeState.accent.copy(alpha = 0.3f)
                                    else ThemeState.cardBg)
                        .clickable { tab = t }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(t.label,
                        color = if (sel) ThemeState.text else ThemeState.textDim,
                        fontSize = 13.sp,
                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (tab) {
                    LibraryTab.SONGS -> {
                        sortedLetters.forEach { letter ->
                            item(key = "header_$letter") {
                                Text(letter.toString(),
                                    color = ThemeState.accent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp))
                            }
                            val songsInLetter = grouped[letter] ?: emptyList()
                            items(songsInLetter, key = { it.id }) { song ->
                                SongItem(
                                    song = song,
                                    isPlaying = song.id == currentSong?.id,
                                    onClick = { onSongClick(song) },
                                    showActions = false
                                )
                            }
                        }
                    }
                    LibraryTab.ALBUMS -> {
                        val albums = allSongs.groupBy { "${it.album} · ${it.artist}" }
                        items(albums.keys.toList()) { key ->
                            Box(Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ThemeState.cardBg)
                                .padding(16.dp)) {
                                Text(key, color = ThemeState.text, fontSize = 15.sp)
                            }
                        }
                    }
                    LibraryTab.ARTISTS -> {
                        val artists = allSongs.groupBy { it.artist }
                        items(artists.keys.toList()) { key ->
                            Box(Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ThemeState.cardBg)
                                .padding(16.dp)) {
                                Text(key, color = ThemeState.text, fontSize = 15.sp)
                            }
                        }
                    }
                    LibraryTab.FOLDERS -> {
                        val folders = allSongs.groupBy {
                            it.path.substringBeforeLast("/").substringAfterLast("/").ifBlank { "未知" }
                        }
                        items(folders.keys.toList()) { key ->
                            Box(Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ThemeState.cardBg)
                                .padding(16.dp)) {
                                Column {
                                    Text("📁 $key", color = ThemeState.text, fontSize = 15.sp)
                                    Text("${folders[key]?.size ?: 0} 首",
                                        color = ThemeState.textDim,
                                        fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (tab == LibraryTab.SONGS && ThemeState.showPinyinIndex) {
                Column(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                        .pointerInput(sortedLetters) {
                            detectTapGestures { offset ->
                                val itemHeight = size.height / sortedLetters.size.toFloat()
                                val idx = (offset.y / itemHeight).toInt()
                                    .coerceIn(0, sortedLetters.size - 1)
                                if (idx in sortedLetters.indices) {
                                    scope.launch {
                                        var target = 0
                                        for (i in 0 until idx) {
                                            target += 1 + (grouped[sortedLetters[i]]?.size ?: 0)
                                        }
                                        listState.animateScrollToItem(target)
                                    }
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    sortedLetters.forEach { c ->
                        Text(c.toString(),
                            color = ThemeState.textFaint,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.5.dp))
                    }
                }
            }
        }
    }
}