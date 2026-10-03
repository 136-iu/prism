package com.example.prism.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.BuiltInEqPresets
import com.example.prism.data.EqPreset
import com.example.prism.playback.EqualizerManager
import com.example.prism.ui.theme.ThemeState

private enum class EqTab(val label: String, val icon: String) {
    EDIT("编辑", "🎚"),
    V4("V4", "⚙"),
    AUTOEQ("AutoEQ", "🎧"),
    BUILTIN("内置", "📦"),
    PERSONAL("个人", "👤")
}

@Composable
fun EqualizerScreen(onBack: () -> Unit) {
    val accent = ThemeState.accent
    var tab by remember { mutableStateOf(EqTab.EDIT) }
    val enabled by EqualizerManager.enabled.collectAsState()

    Column(Modifier.fillMaxSize()) {
        // 顶栏
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("均衡器", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
                Text("Redmi Buds 6 Pro", color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp)
            }
            Switch(
                checked = enabled,
                onCheckedChange = { EqualizerManager.setEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accent
                )
            )
        }

        // 内容
        Box(Modifier.weight(1f)) {
            when (tab) {
                EqTab.EDIT -> EqualizerEditView()
                EqTab.BUILTIN -> BuiltinPresetsView()
                EqTab.AUTOEQ -> AutoEqView()
                EqTab.V4 -> PlaceholderEq("V4 参数编辑")
                EqTab.PERSONAL -> PlaceholderEq("个人预设")
            }
        }

        // 底部 Tab
        Row(
            Modifier.fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EqTab.values().forEach { t ->
                val sel = t == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { tab = t }.padding(6.dp)
                ) {
                    Text(t.icon, fontSize = 18.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(t.label,
                        color = if (sel) accent else Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun EqualizerEditView() {
    val accent = ThemeState.accent
    val bands by EqualizerManager.bandLevels.collectAsState()
    val range = remember { EqualizerManager.getLevelRangeDb() }
    var selectedBand by remember { mutableStateOf(-1) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // 图形曲线
        Box(
            Modifier.fillMaxWidth().height(280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.04f))
        ) {
            EqCurve(bands = bands, accent = accent, range = range,
                selectedIndex = selectedBand,
                onSelect = { selectedBand = it })
        }

        Spacer(Modifier.height(12.dp))

        // 输入增益
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("输入增益", color = Color.White, fontSize = 13.sp)
            Text("-6.3 dB", color = accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier.fillMaxWidth().height(4.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            Box(Modifier.fillMaxWidth(0.5f).fillMaxHeight()
                .background(accent))
        }

        Spacer(Modifier.height(16.dp))

        // 频段标签
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BuiltInEqPresets.bandLabels.forEach { label ->
                Text(label,
                    color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun EqCurve(
    bands: List<Float>,
    accent: Color,
    range: ClosedFloatingPointRange<Float>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val minDb = range.start
    val maxDb = range.endInclusive

    Canvas(
        Modifier.fillMaxSize()
            .pointerInput(bands.size) {
                detectDragGestures { change, _ ->
                    val x = change.position.x
                    val idx = ((x / size.width) * bands.size).toInt()
                        .coerceIn(0, bands.size - 1)
                    onSelect(idx)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val stepX = w / (bands.size + 1).toFloat()

        // 网格
        for (i in 0..5) {
            val y = h * i / 5f
            drawLine(
                Color.White.copy(alpha = 0.06f),
                Offset(0f, y), Offset(w, y),
                strokeWidth = 1f
            )
        }

        // 曲线
        val path = Path()
        bands.forEachIndexed { i, db ->
            val x = stepX * (i + 1)
            val ratio = ((db - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
            val y = h * (1f - ratio)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, accent, style = Stroke(width = 4f, cap = StrokeCap.Round))

        // 节点
        bands.forEachIndexed { i, db ->
            val x = stepX * (i + 1)
            val ratio = ((db - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
            val y = h * (1f - ratio)
            val isSel = i == selectedIndex
            drawCircle(
                color = if (isSel) Color.White else accent,
                radius = if (isSel) 10f else 7f,
                center = Offset(x, y)
            )
            if (isSel) {
                drawCircle(
                    color = accent,
                    radius = 14f,
                    center = Offset(x, y),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

@Composable
private fun BuiltinPresetsView() {
    val accent = ThemeState.accent
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(BuiltInEqPresets.all) { preset ->
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .clickable { EqualizerManager.applyPreset(preset) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📦", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(preset.name, color = Color.White,
                        fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${preset.bands.count { it != 0f }} 个滤波器  " +
                        (if (preset.preamp >= 0) "+" else "") + "${preset.preamp} dB",
                        color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AutoEqView() {
    val accent = ThemeState.accent
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(14.dp)
            ) {
                Text("🔍 搜索耳机", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("全部", "头戴式", "入耳式", "耳塞式", "来源", "品牌")) { chip ->
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(
                                if (chip == "全部") accent.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(chip, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("8850 个耳机配置",
                color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
        }

        items(listOf(
            "1Custom SA02" to "crinacle 入耳式",
            "1Custom SA03" to "crinacle 入耳式",
            "1MORE Aero (ANC Off)" to "HypetheSonics 入耳式",
            "1MORE Aero (ANC On)" to "HypetheSonics 入耳式",
            "AirPods Pro 2" to "apple 耳塞式",
            "Sony WH-1000XM5" to "sony 头戴式"
        )) { (name, sub) ->
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .clickable { }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🎧", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, color = Color.White,
                        fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Text(sub, color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderEq(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$title（后续版本）",
            color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
    }
}