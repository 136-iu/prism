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
fun HomeSettings(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🏠 主页", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("卡片布局") {
            Text("大卡片位置：左", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("小卡片每行：4 张", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("小卡片行数：6 行", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("在线/离线") {
            Text("默认模式：在线", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("榜单来源：音源提供", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("断网自动切换：开", color = Color.White, fontSize = 14.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}