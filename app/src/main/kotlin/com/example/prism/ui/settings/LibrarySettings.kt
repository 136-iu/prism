package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.ThemeState

@Composable
fun LibrarySettings(onBack: () -> Unit) {
    var showFolder by remember { mutableStateOf(Storage.getLibShowFolder()) }
    var showAlbum by remember { mutableStateOf(Storage.getLibShowAlbum()) }
    var showArtist by remember { mutableStateOf(Storage.getLibShowArtist()) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📚 资料库", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("显示") {
            ToggleRow("显示文件夹", showFolder) {
                showFolder = it; Storage.setLibShowFolder(it)
            }
            ToggleRow("显示专辑", showAlbum) {
                showAlbum = it; Storage.setLibShowAlbum(it)
            }
            ToggleRow("显示歌手", showArtist) {
                showArtist = it; Storage.setLibShowArtist(it)
            }
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("扫描") {
            Text("扫描根目录：/sdcard/Music",
                color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("忽略 <30 秒音频：开",
                color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("忽略系统铃声：开",
                color = ThemeState.textDim, fontSize = 13.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}