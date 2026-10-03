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
fun LibrarySettings(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📚 资料库", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("显示") {
            Text("显示文件夹：开", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("显示专辑：开", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("显示歌手：开", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("显示歌曲数：开", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("排序方式：名称", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("文件夹") {
            Text("扫描根目录：/sdcard/Music", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("忽略短音频（<30秒）：开", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("忽略系统铃声：开", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}