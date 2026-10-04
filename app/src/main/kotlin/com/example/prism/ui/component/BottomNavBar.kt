package com.example.prism.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass

enum class NavTab(val label: String, val icon: ImageVector) {
    HOME("主页", Icons.Filled.Home),
    LIBRARY("资料库", Icons.Filled.LibraryMusic),
    SEARCH("搜索", Icons.Filled.Search),
    SETTINGS("设置", Icons.Filled.Settings)
}

@Composable
fun BottomNavBar(
    current: NavTab,
    onSelect: (NavTab) -> Unit,
    onLongPressSearch: (() -> Unit)? = null
) {
    val accent = ThemeState.accent
    val dark = ThemeState.isDark
    val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f

    var barWidth by remember { mutableFloatStateOf(0f) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val tabs = NavTab.values()

    // 拖动跟手时整体水平偏移
    val visualOffset by animateFloatAsState(
        targetValue = if (isDragging) dragOffset * 0.3f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "navDragOffset"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(60.dp)
            .onSizeChanged { barWidth = it.width.toFloat() }
            .graphicsLayer { translationX = visualOffset }
            .liquidGlass(
                RoundedCornerShape(30.dp), dark, ThemeState.glassTint, gi
            )
            .pointerInput(current) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragOffset = 0f
                    },
                    onDragEnd = {
                        isDragging = false
                        if (barWidth > 0f) {
                            val tabWidth = barWidth / tabs.size
                            val currentIndex = tabs.indexOf(current)
                            val startCenter = tabWidth * (currentIndex + 0.5f)
                            val endCenter = startCenter + dragOffset
                            val targetIndex = (endCenter / tabWidth).toInt()
                                .coerceIn(0, tabs.size - 1)
                            if (targetIndex != currentIndex) {
                                onSelect(tabs[targetIndex])
                            }
                        }
                        dragOffset = 0f
                    },
                    onDrag = { change, drag ->
                        dragOffset += drag.x
                        // 限制拖动范围
                        dragOffset = dragOffset.coerceIn(
                            -barWidth, barWidth
                        )
                        change.consume()
                    }
                )
            }
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val selected = tab == current
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.08f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "navScale"
            )

            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (selected) accent.copy(alpha = 0.22f)
                        else Color.Transparent
                    )
                    .clickable {
                        // 搜索 tab 用长按触发全屏搜索
                        onSelect(tab)
                    }
                    .padding(vertical = 6.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) accent else ThemeState.textDim,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    tab.label,
                    color = if (selected) accent else ThemeState.textDim,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}