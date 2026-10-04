package com.example.prism.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prism.data.Storage
import com.example.prism.ui.theme.ThemeState

@Composable
fun SearchSettings(onBack: () -> Unit) {
    var saveHistory by remember { mutableStateOf(Storage.getSearchHistoryEnabled()) }
    var scope by remember { mutableStateOf(Storage.getSearchScope()) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        SettingsHeader("🔍 搜索", onBack)
        Spacer(Modifier.height(20.dp))

        SectionCard("搜索") {
            Text("默认范围：$scope", color = ThemeState.textDim, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            ToggleRow("保存搜索历史", saveHistory) {
                saveHistory = it; Storage.setSearchHistoryEnabled(it)
            }
            Spacer(Modifier.height(8.dp))
            Text("最多保存：20 条", color = ThemeState.textDim, fontSize = 13.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}