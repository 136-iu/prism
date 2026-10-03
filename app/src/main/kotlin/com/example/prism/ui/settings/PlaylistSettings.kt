package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaylistSettings(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📁 歌单", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("导入/导出") {
            Text("默认导出格式：M3U", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("默认导出位置：Download", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("导入时自动匹配：开", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("操作") {
            Text("📤 导出歌单", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("📥 导入歌单", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}