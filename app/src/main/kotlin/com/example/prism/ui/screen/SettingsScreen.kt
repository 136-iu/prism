package com.example.prism.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.MainOverlay
import com.example.prism.ui.theme.ThemeState

@Composable
fun SettingsScreen(onNavigate: (MainOverlay) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("⚙️ 设置", color = ThemeState.text, fontSize = 24.sp,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        SettingRow("🎨", "外观", "主题 · 玻璃 · 字体") { onNavigate(MainOverlay.APPEARANCE) }
        SettingRow("🎬", "动画", "回弹 · 过渡") { onNavigate(MainOverlay.ANIMATION) }
        SettingRow("🏠", "主页", "卡片 · 布局") { onNavigate(MainOverlay.HOME_SETTINGS) }
        SettingRow("📚", "资料库", "显示 · 文件夹") { onNavigate(MainOverlay.LIBRARY_SETTINGS) }
        SettingRow("📁", "歌单", "导入 · 导出") { onNavigate(MainOverlay.PLAYLIST_SETTINGS) }
        SettingRow("▶️", "播放", "音质 · 音效") { onNavigate(MainOverlay.PLAYBACK_SETTINGS) }
        SettingRow("📝", "歌词", "显示 · 翻译") { onNavigate(MainOverlay.LYRICS_SETTINGS) }
        SettingRow("🔍", "搜索", "范围 · 历史") { onNavigate(MainOverlay.SEARCH_SETTINGS) }
        SettingRow("🔌", "扩展", "音源 · AI 模型") { onNavigate(MainOverlay.EXTENSIONS) }
        SettingRow("📱", "小组件", "尺寸 · 样式") { onNavigate(MainOverlay.WIDGET_SETTINGS) }
        SettingRow("💾", "存储", "缓存 · 数据") { onNavigate(MainOverlay.STORAGE) }
        SettingRow("🧪", "实验室", "实验功能") { onNavigate(MainOverlay.LAB) }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SettingRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ThemeState.cardBg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(ThemeState.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = ThemeState.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = ThemeState.textDim, fontSize = 12.sp)
        }
        Text("›", color = ThemeState.textFaint, fontSize = 22.sp)
    }
}