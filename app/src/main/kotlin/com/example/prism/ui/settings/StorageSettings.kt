package com.example.prism.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StorageSettings(onBack: () -> Unit) {
    val context = LocalContext.current
    val toast: (String) -> Unit = { msg ->
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("💾 存储", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("缓存") {
            Text("封面缓存：0 MB", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("歌词缓存：0 MB", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("搜索缓存：0 MB", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Text("🗑️ 清除缓存", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp,
                modifier = Modifier.clickable { toast("功能开发中") })
        }

        Spacer(Modifier.height(12.dp))

        SectionCard("数据") {
            Text("📤 导出配置", color = Color.White, fontSize = 14.sp,
                modifier = Modifier.clickable { toast("功能开发中") })
            Spacer(Modifier.height(8.dp))
            Text("📥 导入配置", color = Color.White, fontSize = 14.sp,
                modifier = Modifier.clickable { toast("功能开发中") })
            Spacer(Modifier.height(8.dp))
            Text("📤 导出听歌记录", color = Color.White, fontSize = 14.sp,
                modifier = Modifier.clickable { toast("功能开发中") })
            Spacer(Modifier.height(12.dp))
            Text("⚠️ 清空全部数据", color = Color(0xFFFF5252), fontSize = 14.sp,
                modifier = Modifier.clickable { toast("功能开发中") })
        }

        Spacer(Modifier.height(40.dp))
    }
}
