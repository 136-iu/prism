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
fun LyricsSettings(onBack: () -> Unit) {
    var autoScroll by remember { mutableStateOf(true) }
    var autoDetectEnglish by remember { mutableStateOf(true) }
    var promptAi by remember { mutableStateOf(true) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📝 歌词", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("显示") {
            Text("字号：中", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("对齐：左对齐", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("当前行高亮：开", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("翻译") {
            ToggleRow("自动检测英文", autoDetectEnglish) { autoDetectEnglish = it }
            ToggleRow("提示配置 AI", promptAi) { promptAi = it }
            Spacer(Modifier.height(8.dp))
            Text("使用的 AI：未配置", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("显示方式：原文+翻译", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("滚动") {
            ToggleRow("自动滚动歌词", autoScroll) { autoScroll = it }
        }

        Spacer(Modifier.height(40.dp))
    }
}