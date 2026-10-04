package com.example.prism.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.ThemeState

@Composable
fun HomeSettings(onBack: () -> Unit) {
    var bigSide by remember { mutableStateOf(Storage.getHomeBigSide()) }
    var smallCols by remember { mutableStateOf(Storage.getHomeSmallCols()) }
    var smallRows by remember { mutableStateOf(Storage.getHomeSmallRows()) }
    var defaultOnline by remember { mutableStateOf(Storage.getDefaultOnline()) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🏠 主页", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("卡片布局") {
            ChoiceRow("大卡片位置", listOf("left" to "左", "right" to "右"), bigSide) {
                bigSide = it; Storage.setHomeBigSide(it)
            }
            Spacer(Modifier.height(12.dp))
            NumberRow("小卡片每行", listOf(3, 4, 5), smallCols) {
                smallCols = it; Storage.setHomeSmallCols(it)
            }
            Spacer(Modifier.height(12.dp))
            NumberRow("小卡片行数", listOf(2, 3, 4, 5, 6, 8), smallRows) {
                smallRows = it; Storage.setHomeSmallRows(it)
            }
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("在线/离线") {
            ToggleRow("默认在线模式", defaultOnline) {
                defaultOnline = it; Storage.setDefaultOnline(it)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ChoiceRow(
    label: String,
    options: List<Pair<String, String>>,
    current: String,
    onPick: (String) -> Unit
) {
    Column {
        Text(label, color = ThemeState.text, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (id, name) ->
                val selected = id == current
                Box(
                    Modifier.clip(CircleShape)
                        .background(
                            if (selected) ThemeState.accent.copy(alpha = 0.3f)
                            else ThemeState.cardBg
                        )
                        .clickable { onPick(id) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(name, color = ThemeState.text, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun NumberRow(
    label: String,
    options: List<Int>,
    current: Int,
    onPick: (Int) -> Unit
) {
    Column {
        Text(label, color = ThemeState.text, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { n ->
                val selected = n == current
                Box(
                    Modifier.clip(CircleShape)
                        .background(
                            if (selected) ThemeState.accent.copy(alpha = 0.3f)
                            else ThemeState.cardBg
                        )
                        .clickable { onPick(n) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("$n", color = ThemeState.text, fontSize = 13.sp)
                }
            }
        }
    }
}