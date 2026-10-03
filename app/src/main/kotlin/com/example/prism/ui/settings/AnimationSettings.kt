package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState

@Composable
fun AnimationSettings(onBack: () -> Unit) {
    var intensity by remember { mutableFloatStateOf(0.5f) }
    var squash by remember { mutableStateOf(false) }
    var stagger by remember { mutableStateOf(true) }
    var ripple by remember { mutableStateOf(false) }
    var shake by remember { mutableStateOf(false) }
    var breathe by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🎬 动画", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("回弹强度 ${(intensity * 100).toInt()}%") {
            Slider(
                value = intensity,
                onValueChange = { intensity = it },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = ThemeState.accent,
                    activeTrackColor = ThemeState.accent
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("效果开关") {
            ToggleRow("压扁效果", squash) { squash = it }
            ToggleRow("依次弹起", stagger) { stagger = it }
            ToggleRow("撞击波纹", ripple) { ripple = it }
            ToggleRow("轻微晃动", shake) { shake = it }
            ToggleRow("呼吸脉动", breathe) { breathe = it }
        }

        Spacer(Modifier.height(40.dp))
    }
}