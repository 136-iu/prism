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
fun WidgetSettings(onBack: () -> Unit) {
    var showCover by remember { mutableStateOf(true) }
    var showProgress by remember { mutableStateOf(true) }
    var followCover by remember { mutableStateOf(true) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📱 小组件", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("样式") {
            Text("背景样式：液态玻璃", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            ToggleRow("显示封面", showCover) { showCover = it }
            ToggleRow("显示进度条", showProgress) { showProgress = it }
            ToggleRow("跟随封面取色", followCover) { followCover = it }
        }

        Spacer(Modifier.height(40.dp))
    }
}