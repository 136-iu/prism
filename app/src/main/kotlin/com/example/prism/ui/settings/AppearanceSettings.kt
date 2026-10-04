package com.example.prism.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.PlayerStyle
import com.example.prism.ui.theme.ThemePresets
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass

@Composable
fun AppearanceSettings(onBack: () -> Unit) {
    val accent = ThemeState.accent
    var isDark by remember { mutableStateOf(ThemeState.isDark) }
    var glassEnabled by remember { mutableStateOf(ThemeState.glassEnabled) }
    var glassIntensity by remember { mutableStateOf(ThemeState.glassIntensity) }
    var playerStyle by remember { mutableStateOf(ThemeState.playerStyle) }
    var fontFollowSystem by remember { mutableStateOf(ThemeState.fontFollowSystem) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🎨 外观", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("主题模式") {
            ToggleRow(
                label = "深色模式",
                checked = isDark,
                onChange = {
                    isDark = it
                    ThemeState.isDark = it
                    Storage.setDark(it)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

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

        SectionCard("液态玻璃") {
            ToggleRow("启用", glassEnabled) {
                glassEnabled = it
                ThemeState.glassEnabled = it
                Storage.setGlassEnabled(it)
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

        SectionCard("字体") {
            ToggleRow("跟随系统", fontFollowSystem) {
                fontFollowSystem = it
                ThemeState.fontFollowSystem = it
                Storage.setFontFollowSystem(it)
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

// ============ 通用组件（所有设置页共用）============

@Composable
fun SettingsHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp)
                .liquidGlass(
                    CircleShape, ThemeState.isDark,
                    ThemeState.glassTint, ThemeState.glassIntensity
                )
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
            .liquidGlass(
                RoundedCornerShape(16.dp),
                ThemeState.isDark,
                ThemeState.glassTint,
                ThemeState.glassIntensity
            )
            .padding(16.dp)
    ) {
        if (title.isNotBlank()) {
            Text(title, color = ThemeState.text, fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold)
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
        IOSSwitch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
fun IOSSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accent: Color = ThemeState.accent
) {
    val dark = ThemeState.isDark
    val trackOff = if (dark) Color(0xFF39393D) else Color(0xFFE9E9EA)

    val trackColor by animateColorAsState(
        targetValue = if (checked) accent else trackOff,
        animationSpec = tween(200),
        label = "trackColor"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "thumbOffset"
    )

    Box(
        Modifier
            .size(width = 50.dp, height = 30.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset, y = 2.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.White)
                .shadow(if (checked) 3.dp else 1.dp, CircleShape)
        )
    }
}