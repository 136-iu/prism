package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabSettings(onBack: () -> Unit) {
    var staggeredScroll by remember { mutableStateOf(false) }
    var threeDimensionalLyrics by remember { mutableStateOf(true) }
    var autoOpenPlayer by remember { mutableStateOf(false) }
    var folderCoverFallback by remember { mutableStateOf(false) }
    var saltUiMaterial by remember { mutableStateOf(true) }
    var liquidGlass by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🧪 实验室", onBack)
        Spacer(Modifier.height(8.dp))
        Text("欢迎使用 Prism 实验室，这里的功\u80fd并非稳定仅供体验，且可能随时被移除",
            color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

        Spacer(Modifier.height(20.dp))

        Text("歌词", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        SectionCard("") {
            ToggleRow("交错滚动效果", staggeredScroll) { staggeredScroll = it }
            ToggleRow("立体歌词效果（开发型测试）", threeDimensionalLyrics) {
                threeDimensionalLyrics = it
            }
            ToggleRow("启动软件自动打开播放界面", autoOpenPlayer) { autoOpenPlayer = it }
            ToggleRow("同文件夹封面回退", folderCoverFallback) { folderCoverFallback = it }
            if (folderCoverFallback) {
                Spacer(Modifier.height(4.dp))
                Text("⚠️ 谨慎使用，可能严重影响图片加载速度",
                    color = Color(0xFFFFB74D), fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Material", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        SectionCard("") {
            ToggleRow("Salt UI Material", saltUiMaterial) {
                saltUiMaterial = it
                if (it) liquidGlass = false
            }
            ToggleRow("Liquid Glass (Android 13+)", liquidGlass) {
                liquidGlass = it
                if (it) saltUiMaterial = false
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}