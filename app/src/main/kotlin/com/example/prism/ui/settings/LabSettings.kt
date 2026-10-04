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
fun LabSettings(onBack: () -> Unit) {
    var staggeredScroll by remember { mutableStateOf(Storage.getLabFlag(Storage.labStaggeredKey)) }
    var threeD by remember { mutableStateOf(Storage.getLabFlag(Storage.lab3DKey, true)) }
    var autoOpenPlayer by remember { mutableStateOf(Storage.getLabFlag(Storage.labAutoPlayerKey)) }
    var folderCover by remember { mutableStateOf(Storage.getLabFlag(Storage.labFolderCoverKey)) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🧪 实验室", onBack)
        Spacer(Modifier.height(8.dp))
        Text("欢迎使用 Prism 实验室，功能非稳定仅供体验",
            color = ThemeState.textDim, fontSize = 12.sp)

        Spacer(Modifier.height(20.dp))

        SectionCard("歌词") {
            ToggleRow("交错滚动效果", staggeredScroll) {
                staggeredScroll = it; Storage.setLabFlag(Storage.labStaggeredKey, it)
            }
            ToggleRow("立体歌词效果（开发型测试）", threeD) {
                threeD = it; Storage.setLabFlag(Storage.lab3DKey, it)
            }
            ToggleRow("启动软件自动打开播放界面", autoOpenPlayer) {
                autoOpenPlayer = it; Storage.setLabFlag(Storage.labAutoPlayerKey, it)
            }
            ToggleRow("同文件夹封面回退", folderCover) {
                folderCover = it; Storage.setLabFlag(Storage.labFolderCoverKey, it)
            }
            if (folderCover) {
                Spacer(Modifier.height(4.dp))
                Text("⚠️ 谨慎使用，可能影响图片加载速度",
                    color = ThemeState.accent, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}