package com.example.prism.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.util.LyricLine
import kotlin.math.abs

@Composable
fun LyricsView(
    lines: List<LyricLine>,
    currentIndex: Int,
    inLineProgress: Float,
    onLineClick: (LyricLine) -> Unit,
    onLineLongClick: (LyricLine) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (currentIndex in lines.indices) {
            listState.animateScrollToItem(
                index = (currentIndex - 2).coerceAtLeast(0),
                scrollOffset = -80
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 240.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        itemsIndexed(lines) { index, line ->
            val isCurrent = index == currentIndex
            val distance = abs(index - currentIndex)
            val alpha = when {
                isCurrent -> 1f
                distance == 1 -> 0.55f
                distance == 2 -> 0.32f
                else -> 0.15f
            }

            LyricLineItem(
                text = line.text,
                isCurrent = isCurrent,
                progress = if (isCurrent) inLineProgress else 0f,
                alpha = alpha,
                onClick = { onLineClick(line) },
                onLongClick = { onLineLongClick(line) }
            )
            Spacer(Modifier.height(18.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LyricLineItem(
    text: String,
    isCurrent: Boolean,
    progress: Float,
    alpha: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val baseColor = Color.White.copy(alpha = alpha)
    val highlightColor = Color.White

    val annotated = remember(text, isCurrent, progress, alpha) {
        if (!isCurrent) {
            buildAnnotatedString {
                withStyle(SpanStyle(color = baseColor)) { append(text) }
            }
        } else {
            // 逐字高亮
            val chars = text.toList()
            val total = chars.size
            val highlightCount = (total * progress).toInt().coerceIn(0, total)
            buildAnnotatedString {
                chars.forEachIndexed { i, c ->
                    val color = if (i < highlightCount) highlightColor else baseColor
                    withStyle(SpanStyle(color = color)) { append(c) }
                }
            }
        }
    }

    Text(
        text = annotated,
        fontSize = if (isCurrent) 30.sp else 24.sp,
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
        lineHeight = if (isCurrent) 42.sp else 34.sp,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    )
}