package com.example.prism.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemePresets
import com.example.prism.ui.theme.ThemeState

@Composable
fun AppearanceSettings(onBack: () -> Unit) {
    val accent = ThemeState.accent
    var isDark by remember { mutableStateOf(ThemeState.isDark) }
    var glassEnabled by remember { mutableStateOf(ThemeState.glassEnabled) }
    var glassIntensity by remember { mutableStateOf(ThemeState.glassIntensity) }
    var playerStyle by remember { mutableStateOf(ThemeState.playerStyle) }
    var fontScale by remember { mutableStateOf(ThemeState.fontScale) }
    var fontFollowSystem by remember { mutableStateOf(ThemeState.fontFollowSystem) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🎨 外观", onBack)
        Spacer(Modifier.height(20.dp))

        // ★ 主题模式
        SectionCard("主题模式") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("深色模式", color = ThemeState.text, fontSize = 14.sp)
                Switch(
                    checked = isDark,
                    onCheckedChange = {
                        isDark = it
                        ThemeState.isDark = it
                        Storage.setDark(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ★ 主题色
        SectionCard("主题色") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ThemePresets.solids.forEach { preset ->
                    val selected = ThemeState.presetId == preset.id
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(if (selected) 42.dp else 34.dp)
                                .clip(CircleShape)
                                .background(Color(preset.value))
                                .clickable {
                                    ThemeState.setAccent(preset.id, preset.value)
                                    Storage.setAccent(preset.id, preset.value)
                                }
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(preset.name, color = ThemeState.textDim, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 液态玻璃
        SectionCard("液态玻璃") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("启用", color = ThemeState.text, fontSize = 14.sp)
                Switch(
                    checked = glassEnabled,
                    onCheckedChange = {
                        glassEnabled = it
                        ThemeState.glassEnabled = it
                        Storage.setGlassEnabled(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
            if (glassEnabled) {
                Spacer(Modifier.height(8.dp))
                Text("强度 ${(glassIntensity * 100).toInt()}%",
                    color = ThemeState.textDim, fontSize = 13.sp)
                Slider(
                    value = glassIntensity,
                    onValueChange = {
                        glassIntensity = it
                        ThemeState.glassIntensity = it
                        Storage.setGlassIntensity(it)
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = accent,
                        activeTrackColor = accent
                    )
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("磨砂", color = ThemeState.textFaint, fontSize = 11.sp)
                    Text("满血", color = ThemeState.textFaint, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 播放页风格
        SectionCard("播放页风格") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StyleButton("Salt Player", playerStyle == PlayerStyle.SALT, Modifier.weight(1f)) {
                    playerStyle = PlayerStyle.SALT
                    ThemeState.playerStyle = PlayerStyle.SALT
                    ThemeState.playerStyleChosen = true
                    Storage.setPlayerStyle("SALT")
                    Storage.setPlayerStyleChosen(true)
                }
                StyleButton("iOS 26", playerStyle == PlayerStyle.IOS26, Modifier.weight(1f)) {
                    playerStyle = PlayerStyle.IOS26
                    ThemeState.playerStyle = PlayerStyle.IOS26
                    ThemeState.playerStyleChosen = true
                    Storage.setPlayerStyle("IOS26")
                    Storage.setPlayerStyleChosen(true)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ★ 字体
        SectionCard("字体") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("跟随系统", color = ThemeState.text, fontSize = 14.sp)
                Switch(
                    checked = fontFollowSystem,
                    onCheckedChange = {
                        fontFollowSystem = it
                        ThemeState.fontFollowSystem = it
                        Storage.setFontFollowSystem(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
            if (!fontFollowSystem) {
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.85f to "小", 1f to "中", 1.15f to "大", 1.3f to "超大").forEach { (scale, label) ->
                        val selected = fontScale == scale
                        Box(
                            Modifier.weight(1f).clip(CircleShape)
                                .background(if (selected) accent.copy(alpha = 0.3f)
                                            else ThemeState.cardBg)
                                .clickable {
                                    fontScale = scale
                                    ThemeState.fontScale = scale
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label,
                                color = if (selected) ThemeState.text else ThemeState.textDim,
                                fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StyleButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(CircleShape)
            .background(if (selected) ThemeState.accent.copy(alpha = 0.3f)
                        else ThemeState.cardBg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = ThemeState.text, fontSize = 13.sp)
    }
}

// ---------- 三个通用组件（所有设置子页共用）----------

@Composable
fun SettingsHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).clip(CircleShape)
                .background(ThemeState.iconBg)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("←", color = ThemeState.text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = ThemeState.text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ThemeState.cardBg)
            .padding(16.dp)
    ) {
        if (title.isNotBlank()) {
            Text(title, color = ThemeState.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
        }
        content()
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = ThemeState.text, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ThemeState.accent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}