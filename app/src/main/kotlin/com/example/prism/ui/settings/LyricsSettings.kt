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
fun LyricsSettings(onBack: () -> Unit) {
    var autoScroll by remember { mutableStateOf(Storage.getLyricsAutoScroll()) }
    var autoDetectEn by remember { mutableStateOf(Storage.getLyricsDetectEn()) }
    var promptAi by remember { mutableStateOf(Storage.getLyricsPromptAi()) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📝 歌词", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("显示") {
            Text("字号：中", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("对齐：左对齐", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("当前行高亮：开", color = ThemeState.textDim, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("翻译") {
            ToggleRow("自动检测英文", autoDetectEn) {
                autoDetectEn = it; Storage.setLyricsDetectEn(it)
            }
            ToggleRow("提示配置 AI", promptAi) {
                promptAi = it; Storage.setLyricsPromptAi(it)
            }
            Spacer(Modifier.height(8.dp))
            Text("使用的 AI：未配置", color = ThemeState.textDim, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("滚动") {
            ToggleRow("自动滚动歌词", autoScroll) {
                autoScroll = it; Storage.setLyricsAutoScroll(it)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}