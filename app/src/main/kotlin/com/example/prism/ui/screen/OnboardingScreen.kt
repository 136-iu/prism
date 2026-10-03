package com.example.prism.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemePresets
import com.example.prism.ui.theme.ThemeState

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF0F0F14), Color(0xFF1A1A2E), Color(0xFF16213E))
            )
        )
    ) {
        AnimatedContent(targetState = page, label = "onboarding") { p ->
            when (p) {
                0 -> WelcomePage(onNext = { page = 1 })
                1 -> ThemePage(onNext = { page = 2 })
                2 -> PlayerStylePage(onFinish = onFinish)
            }
        }
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎵", fontSize = 72.sp)
        Spacer(Modifier.height(24.dp))
        Text("欢迎使用", color = Color.White.copy(alpha = 0.7f), fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        Text("棱镜", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "一款精致、强大、开源的本地音乐播放器",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(60.dp))
        OnboardButton("开始配置", onClick = onNext)
        Spacer(Modifier.height(40.dp))
        Text("下一步会花 1 分钟配置基本外观", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
    }
}

@Composable
private fun ThemePage(onNext: () -> Unit) {
    var isDark by remember { mutableStateOf(true) }
    var selectedPresetId by remember { mutableStateOf("purple") }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎨", fontSize = 42.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))
        Text("选择外观", color = Color.White, fontSize = 24.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(32.dp))

        Text("深浅模式", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ThemeOption("深色", isDark) { isDark = true }
            ThemeOption("浅色", !isDark) { isDark = false }
        }

        Spacer(Modifier.height(28.dp))

        Text("主题色", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThemePresets.solids.forEach { preset ->
                val sel = preset.id == selectedPresetId
                Box(
                    Modifier.size(if (sel) 48.dp else 40.dp)
                        .clip(CircleShape)
                        .background(Color(preset.value))
                        .clickable { selectedPresetId = preset.id }
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        OnboardButton("下一步") {
            // 写入
            ThemeState.isDark = isDark
            val preset = ThemePresets.solids.firstOrNull { it.id == selectedPresetId }
            if (preset != null) ThemeState.setAccent(preset.id, preset.value)
            onNext()
        }
    }
}

@Composable
private fun PlayerStylePage(onFinish: () -> Unit) {
    var style by remember { mutableStateOf(PlayerStyle.IOS26) }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("▶️", fontSize = 42.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))
        Text("选择播放页风格", color = Color.White, fontSize = 24.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(8.dp))
        Text("之后可以随时在设置里切换",
            color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(32.dp))

        StyleCard(
            title = "Salt Player 风",
            desc = "纯黑背景 · 大方形封面 · 底部 5 图标",
            selected = style == PlayerStyle.SALT,
            onClick = { style = PlayerStyle.SALT }
        )
        Spacer(Modifier.height(12.dp))
        StyleCard(
            title = "iOS 26 风",
            desc = "封面模糊铺底 · 大歌名 · 右侧圆播放键",
            selected = style == PlayerStyle.IOS26,
            onClick = { style = PlayerStyle.IOS26 }
        )

        Spacer(Modifier.height(48.dp))

        OnboardButton("开始使用") {
            ThemeState.playerStyle = style
            ThemeState.playerStyleChosen = true
            Storage.markLaunched()
            onFinish()
        }
    }
}

@Composable
private fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(if (selected) ThemeState.accent.copy(alpha = 0.3f)
                        else Color.White.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
private fun StyleCard(
    title: String, desc: String,
    selected: Boolean, onClick: () -> Unit
) {
    Box(
        Modifier.fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(if (selected) ThemeState.accent.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun OnboardButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .clip(CircleShape)
            .background(ThemeState.accent)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}