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

    val tabs = NavTab.values()
    var barWidth by remember { mutableFloatStateOf(0f) }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val currentIndex = tabs.indexOf(current)
    val tabWidthPx = if (barWidth > 0f) barWidth / tabs.size else 0f

    // 小椭圆的水平偏移（px）
    val targetOffset = currentIndex * tabWidthPx + dragOffsetPx
    val lensOffset by animateFloatAsState(
        targetValue = if (isDragging) targetOffset else currentIndex * tabWidthPx,
        animationSpec = if (isDragging) {
            spring(stiffness = Spring.StiffnessHigh)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "lensOffset"
    )

    // 拖动时小椭圆稍微放大
    val lensScale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "lensScale"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(64.dp)
    ) {
        // 底栏玻璃
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged {
                    barWidth = it.width.toFloat()
                }
                .liquidGlass(RoundedCornerShape(32.dp), dark, ThemeState.glassTint, gi)
                .pointerInput(current) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragOffsetPx = 0f
                        },
                        onDragEnd = {
                            isDragging = false
                            if (tabWidthPx > 0f) {
                                val endCenter = currentIndex * tabWidthPx + dragOffsetPx
                                val target = (endCenter / tabWidthPx).toInt()
                                    .coerceIn(0, tabs.size - 1)
                                if (target != currentIndex) {
                                    onSelect(tabs[target])
                                }
                            }
                            dragOffsetPx = 0f
                        },
                        onDrag = { change, drag ->
                            dragOffsetPx = (dragOffsetPx + drag.x).coerceIn(
                                -currentIndex * tabWidthPx,
                                (tabs.size - 1 - currentIndex) * tabWidthPx
                            )
                            change.consume()
                        }
                    )
                }
        )

        // iOS 26 风格：悬浮小椭圆（跟随选中/拖动）
        if (tabWidthPx > 0f) {
            Box(
                Modifier
                    .offset(x = 0.dp)
                    .width((tabWidthPx / 2.7f).dp)  // 小椭圆宽度 ≈ 1/3 tab 宽
                    .height(44.dp)
                    .align(Alignment.CenterStart)
                    .graphicsLayer {
                        translationX = lensOffset + (tabWidthPx / 2f) - (tabWidthPx / 5.4f)
                        translationY = 10f
                        scaleX = lensScale
                        scaleY = lensScale
                    }
                    .liquidGlass(
                        RoundedCornerShape(22.dp), dark,
                        accent.copy(alpha = 0.35f), 0.9f
                    )
            )
        }

        // 4 个 tab 内容（图标 + 文字），铺在椭圆上面
        Row(
            Modifier.fillMaxSize().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = tab == current
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelect(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
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
                            fontWeight = if (selected) FontWeight.SemiBold
                                         else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
