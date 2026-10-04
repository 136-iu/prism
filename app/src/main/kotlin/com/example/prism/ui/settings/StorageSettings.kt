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
import com.example.prism.ui.theme.liquidGlass

@Composable
fun StorageSettings(onBack: () -> Unit) {
    val context = LocalContext.current
    val dark = ThemeState.isDark
    val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("💾 存储", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("缓存") {
            Text("封面缓存：0 MB", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("歌词缓存：0 MB", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("搜索缓存：0 MB", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            ActionButton("🗑️ 清除缓存") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("数据") {
            ActionButton("📤 导出配置") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
            Spacer(Modifier.height(8.dp))
            ActionButton("📥 导入配置") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
            Spacer(Modifier.height(8.dp))
            ActionButton("📤 导出听歌记录") {
                Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
            }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(androidx.compose.ui.graphics.Color(0xFFFF5252).copy(alpha = 0.15f))
                    .clickable {
                        Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("⚠️ 清空全部数据",
                    color = androidx.compose.ui.graphics.Color(0xFFFF5252),
                    fontSize = 14.sp, fontWeight = FontWeight.Medium)
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