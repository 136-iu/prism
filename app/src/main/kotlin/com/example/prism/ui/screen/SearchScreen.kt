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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

private const val KEY_HISTORY = "search_history"

@Composable
fun SearchScreen(
    allSongs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("本地") }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }
    val currentSong by PlayerManager.currentSong.collectAsState()
    val accent = ThemeState.accent

    // 从 storage 加载历史（复用 Storage 的 SharedPreferences）
    LaunchedEffect(Unit) {
        history = loadSearchHistory()
    }

    // 防抖搜索
    var results by remember { mutableStateOf<List<Song>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
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

    val hotKeywords = remember(allSongs) {
        allSongs.take(6).map { it.title }
    }

    Column(Modifier.fillMaxSize()) {
        // 标题
        Text(
            "🔍 搜索",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // 搜索框
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .background(Color.White.copy(alpha = 0.10f), CircleShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔍", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            "搜索歌曲、歌手、专辑",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        cursorBrush = SolidColor(accent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { query = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                } else {
                    // 语音输入（占位，后续接语音识别）
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { /* 语音输入 */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎤", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // 搜索范围
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("全部", "本地", "在线").forEach { s ->
                val sel = s == scope
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(
                            if (sel) accent.copy(alpha = 0.30f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .clickable { scope = s }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        s,
                        color = if (sel) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 内容区
        when {
            query.isBlank() -> {
                // 无输入：显示历史 + 热门
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (history.isNotEmpty()) {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "🕐 搜索历史",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.weight(1f))
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            history = emptyList()
                                            saveSearchHistory(emptyList())
                                        }
                                        .padding(6.dp)
                                ) {
                                    Text("🗑️", fontSize = 14.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(history) { h ->
                                    Box(
                                        Modifier
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .clickable { query = h }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(h, color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (hotKeywords.isNotEmpty()) {
                        item {
                            Text(
                                "🔥 热门搜索",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(hotKeywords) { h ->
                                    Box(
                                        Modifier
                                            .clip(CircleShape)
                                            .background(accent.copy(alpha = 0.20f))
                                            .clickable { query = h }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(h, color = Color.White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (history.isEmpty() && hotKeywords.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(top = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "输入关键词开始搜索",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            searching -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "搜索中…",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            results.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "没有找到 \"$query\"",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                // 保存搜索历史
                LaunchedEffect(query, results) {
                    if (query.isNotBlank() && results.isNotEmpty()) {
                        val newHistory = (listOf(query) + history.filter { it != query })
                            .take(20)
                        history = newHistory
                        saveSearchHistory(newHistory)
                    }
                }

                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Text(
                            "共 ${results.size} 个结果",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
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
}

// ==================== 搜索历史本地存储 ====================

private fun loadSearchHistory(): List<String> {
    return try {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(
            com.example.prism.PrismApplication.appContext
        )
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
        com.google.gson.Gson().fromJson<List<String>>(json, type) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveSearchHistory(list: List<String>) {
    try {
        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(
            com.example.prism.PrismApplication.appContext
        )
        prefs.edit().putString(KEY_HISTORY, com.google.gson.Gson().toJson(list)).apply()
    } catch (_: Exception) {}
}