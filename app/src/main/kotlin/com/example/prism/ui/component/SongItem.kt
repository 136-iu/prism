package com.example.prism.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.prism.data.Song
import com.example.prism.ui.theme.ThemeState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onPlusClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    badgeText: String? = null,
    showActions: Boolean = true
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPlaying) ThemeState.cardBgStrong else ThemeState.cardBg
            )
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(6.dp))
                .background(ThemeState.iconBg),
            contentAlignment = Alignment.Center
        ) {
            if (song.artworkUri != null) {
                AsyncImage(
                    model = song.artworkUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("🎵", fontSize = 20.sp)
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    song.title,
                    color = if (isPlaying) ThemeState.accent else ThemeState.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badgeText != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(4.dp))
                            .background(ThemeState.accent.copy(alpha = 0.8f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(badgeText, color = androidx.compose.ui.graphics.Color.White,
                            fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                song.artist,
                color = ThemeState.textDim,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (showActions) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(50))
                    .clickable(onClick = onPlusClick),
                contentAlignment = Alignment.Center
            ) {
                Text("＋", color = ThemeState.textFaint, fontSize = 18.sp)
            }
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(50))
                    .clickable(onClick = onMoreClick),
                contentAlignment = Alignment.Center
            ) {
                Text("⋮", color = ThemeState.textFaint, fontSize = 18.sp)
            }
        } else {
            Text(song.durationText(), color = ThemeState.textFaint, fontSize = 12.sp)
        }
    }
}