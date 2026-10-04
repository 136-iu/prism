package com.example.prism.ui.settings

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState

@Composable
fun PlaylistSettings(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("📁 歌单", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("导入/导出") {
            Text("默认导出格式：M3U", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("默认导出位置：Download", color = ThemeState.textDim, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("操作") {
            ActionButton("📤 导出歌单") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
            Spacer(Modifier.height(8.dp))
            ActionButton("📥 导入歌单") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ThemeState.accent.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = ThemeState.text, fontSize = 14.sp,
            fontWeight = FontWeight.Medium)
    }
}