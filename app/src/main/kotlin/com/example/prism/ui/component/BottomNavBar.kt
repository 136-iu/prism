package com.example.prism.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass
import kotlinx.coroutines.delay
import kotlin.math.abs

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
    val tabs = NavTab.values()
    val density = LocalDensity.current
    val dark = ThemeState.isDark
    val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f
    val accent = ThemeState.accent

    var containerWidthPx by remember { mutableStateOf(0f) }
    val tabWidthPx = if (containerWidthPx > 0f) containerWidthPx / tabs.size else 0f
    val selectedIndex = tabs.indexOf(current)

    var draggingOffsetPx by remember { mutableStateOf<Float?>(null) }
    val isDragging = draggingOffsetPx != null
    val targetOffsetPx = tabWidthPx * selectedIndex

    var pressStart by remember { mutableLongStateOf(0L) }
    var isDraggingNow by remember { mutableStateOf(false) }
    var longPressActive by remember { mutableStateOf(false) }

    LaunchedEffect(pressStart, isDraggingNow) {
        if (pressStart > 0L && !isDraggingNow) {
            delay(400L)
            if (pressStart > 0L && !isDraggingNow) {
                longPressActive = true
            }
        }
    }

    val animOffsetPx by animateFloatAsState(
        targetValue = draggingOffsetPx ?: targetOffsetPx,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "indicator"
    )

    val nearestIndex = if (isDragging && tabWidthPx > 0f) {
        ((animOffsetPx + tabWidthPx / 2f) / tabWidthPx).toInt().coerceIn(0, tabs.size - 1)
    } else selectedIndex

    val lensScale by animateFloatAsState(
        targetValue = when {
            isDragging -> 1.10f
            longPressActive -> 1.15f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "lensScale"
    )

    Box(
        Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .height(60.dp)
    ) {
        Box(
            Modifier.fillMaxSize()
                .onSizeChanged { containerWidthPx = it.width.toFloat() }
                .liquidGlass(RoundedCornerShape(30.dp), dark, ThemeState.glassTint, gi)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = if (dark) 0.4f else 0.75f),
                            Color.White.copy(alpha = if (dark) 0.1f else 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .pointerInput(tabs.size, tabWidthPx) {
                    if (tabWidthPx <= 0f) return@pointerInput
                    val maxOffset = tabWidthPx * (tabs.size - 1)
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        pressStart = System.currentTimeMillis()
                        isDraggingNow = false
                        longPressActive = false

                        var isDrag = false
                        var offset = (down.position.x - tabWidthPx / 2f)
                            .coerceIn(0f, maxOffset)

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break

                            if (change.pressed) {
                                val delta = change.positionChange().x
                                if (!isDrag && abs(delta) > 3f) {
                                    isDrag = true
                                    isDraggingNow = true
                                    draggingOffsetPx = offset
                                }
                                if (isDrag) {
                                    change.consume()
                                    offset = (offset + delta).coerceIn(0f, maxOffset)
                                    draggingOffsetPx = offset
                                }
                            } else {
                                val finalIdx: Int = if (isDrag) {
                                    ((offset + tabWidthPx / 2f) / tabWidthPx)
                                        .toInt().coerceIn(0, tabs.size - 1)
                                } else {
                                    (down.position.x / tabWidthPx)
                                        .toInt().coerceIn(0, tabs.size - 1)
                                }
                                draggingOffsetPx = null
                                pressStart = 0L
                                isDraggingNow = false
                                longPressActive = false
                                onSelect(tabs[finalIdx])
                                break
                            }
                        }
                    }
                }
        ) {
            if (tabWidthPx > 0f) {
                val tabWidthDp = with(density) { tabWidthPx.toDp() }
                val offsetDp = with(density) { animOffsetPx.toDp() }

                Box(
                    Modifier
                        .offset(x = offsetDp + 8.dp)
                        .padding(vertical = 8.dp)
                        .width(tabWidthDp - 16.dp)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = lensScale
                            scaleY = lensScale
                        }
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                            .liquidGlass(
                                RoundedCornerShape(24.dp),
                                dark,
                                accent.copy(alpha = 0.30f),
                                0.95f
                            )
                    ) {
                        Box(
                            Modifier.fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        center = Offset(
                                            x = tabWidthPx * 0.5f,
                                            y = with(density) { 30.dp.toPx() }
                                        ),
                                        radius = with(density) { 90.dp.toPx() },
                                        colors = listOf(
                                            Color.White.copy(
                                                alpha = when {
                                                    longPressActive -> 0.55f
                                                    isDragging -> 0.45f
                                                    else -> 0.28f
                                                }
                                            ),
                                            Color.White.copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            Row(Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, tab ->
                    val isNearest = isDragging && index == nearestIndex
                    val isSelected = !isDragging && tab == current
                    val isActive = isSelected || isNearest

                    val targetFontSize = when {
                        isDragging && isNearest -> 13f
                        isDragging -> 10f
                        isSelected -> 11f
                        else -> 10f
                    }
                    val fontSizeValue by animateFloatAsState(
                        targetValue = targetFontSize,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "fontSize"
                    )
                    val textAlpha = when {
                        isDragging && !isNearest -> 0.5f
                        isDragging -> 1f
                        isSelected -> 1f
                        else -> 0.75f
                    }
                    val textAlphaValue by animateFloatAsState(
                        targetValue = textAlpha,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "textAlpha"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (isActive) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )

                    Box(
                        Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                tab.icon,
                                contentDescription = tab.label,
                                tint = Color.White.copy(alpha = textAlphaValue),
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                tab.label,
                                color = Color.White.copy(alpha = textAlphaValue),
                                fontSize = fontSizeValue.sp,
                                fontWeight = if (isActive) FontWeight.SemiBold
                                             else FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}