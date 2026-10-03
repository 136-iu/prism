package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Playlist
import com.example.prism.data.PlaylistStore
import com.example.prism.ui.theme.ThemeState

@Composable
fun PlaylistScreen(onBack: () -> Unit) {
    val accent = ThemeState.accent
    var playlists by remember { mutableStateOf(PlaylistStore.load()) }
    var showCreate by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Color(0xFF0F0F14))) {
        Column(Modifier.fillMaxSize().systemBarsPadding()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, "返回", tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text("📁 我的歌单", color = Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(accent.copy(alpha = 0.3f))
                        .clickable { showCreate = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, "新建", tint = Color.White)
                }
            }

            if (playlists.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有歌单\n点击右上角 + 新建",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists) { pl ->
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📁", fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(pl.name, color = Color.White, fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium)
                                Text("${pl.songIds.size} 首",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp)
                            }
                            Box(
                                Modifier.size(32.dp).clip(CircleShape)
                                    .background(Color(0xFFFF5252).copy(alpha = 0.8f))
                                    .clickable {
                                        PlaylistStore.delete(pl.id)
                                        playlists = PlaylistStore.load()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🗑️", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("新建歌单", color = Color.White) },
            text = {
                BasicTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush = SolidColor(accent),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        PlaylistStore.create(newName.trim())
                        playlists = PlaylistStore.load()
                        newName = ""
                        showCreate = false
                    }
                }) { Text("创建", color = accent, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false; newName = "" }) {
                    Text("取消", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1A1A2E)
        )
    }
}