package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState

@Composable
fun PlaybackSettings(onBack: () -> Unit) {
    var gapless by remember { mutableStateOf(true) }
    var fadeIn by remember { mutableStateOf(true) }
    var fadeDuration by remember { mutableFloatStateOf(1.5f) }
    var resumeOnBoot by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(1f) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("▶️ 播放", onBack)
        Spacer(Modifier.height(20.dp))
        SectionCard("播放行为") {
            ToggleRow("无缝播放", gapless) { gapless = it }
            ToggleRow("淡入淡出", fadeIn) { fadeIn = it }
            ToggleRow("启动时恢复上次播放", resumeOnBoot) { resumeOnBoot = it }
        }
        Spacer(Modifier.height(12.dp))
        SectionCard("淡入时长 ${"%.1f".format(fadeDuration)} 秒") {
            Slider(
                value = fadeDuration,
                onValueChange = { fadeDuration = it },
                valueRange = 0f..3f,
                colors = SliderDefaults.colors(
                    thumbColor = ThemeState.accent,
                    activeTrackColor = ThemeState.accent
                )
            )
        }
        Spacer(Modifier.height(12.dp))
        SectionCard("播放速度 ${"%.2f".format(speed)}x") {
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0.5f..3f,
                colors = SliderDefaults.colors(
                    thumbColor = ThemeState.accent,
                    activeTrackColor = ThemeState.accent
                )
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}
