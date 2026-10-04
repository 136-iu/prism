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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.ui.theme.ThemeState
import com.example.prism.ui.theme.liquidGlass

@Composable
fun ExtensionsSettings(onBack: () -> Unit) {
    val context = LocalContext.current
    val dark = ThemeState.isDark
    val gi = if (ThemeState.glassEnabled) ThemeState.glassIntensity else 0f

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🔌 扩展", onBack)
        Spacer(Modifier.height(20.dp))

        Box(
            Modifier.fillMaxWidth()
                .liquidGlass(RoundedCornerShape(16.dp), dark, ThemeState.glassTint, gi)
                .padding(16.dp)
        ) {
            Column {
                Text("🎵 音源管理", color = ThemeState.text, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("导入 .js 音源脚本", color = ThemeState.textDim, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("已导入：0 个", color = ThemeState.textFaint, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallButton("从文件导入") {
                        Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
                    }
                    SmallButton("粘贴代码") {
                        Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            Modifier.fillMaxWidth()
                .liquidGlass(RoundedCornerShape(16.dp), dark, ThemeState.glassTint, gi)
                .padding(16.dp)
        ) {
            Column {
                Text("🤖 AI 模型", color = ThemeState.text, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("添加 OpenAI 兼容 API", color = ThemeState.textDim, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("已配置：0 个", color = ThemeState.textFaint, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                SmallButton("添加 AI 服务商") {
                    Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SmallButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(50))
            .background(ThemeState.accent.copy(alpha = 0.25f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, color = ThemeState.text, fontSize = 12.sp)
    }
}