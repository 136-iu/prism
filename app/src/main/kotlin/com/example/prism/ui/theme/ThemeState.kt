package com.example.prism.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class PlayerStyle { SALT, IOS26 }
enum class LibraryView { LIST, GRID }

object ThemeState {

    var isDark by mutableStateOf(true)
    var accent by mutableStateOf(Color(0xFF7C4DFF))
    var accentValue by mutableStateOf(0xFF7C4DFFL)
    var presetId by mutableStateOf("purple")
    var glassEnabled by mutableStateOf(true)
    var glassIntensity by mutableStateOf(0.55f)

    var fontScale by mutableStateOf(1f)
    var fontFollowSystem by mutableStateOf(true)

    var playerStyle by mutableStateOf(PlayerStyle.IOS26)
    var playerStyleChosen by mutableStateOf(false)
    var libraryView by mutableStateOf(LibraryView.LIST)
    var showPinyinIndex by mutableStateOf(true)

    // ---------- 颜色系统 ----------
    val bgStart: Color
        get() = if (isDark) Color(0xFF0F0F14) else Color(0xFFF7F7FA)
    val bgMid: Color
        get() = if (isDark) Color(0xFF1A1A2E) else Color(0xFFEFEFF4)
    val bgEnd: Color
        get() = if (isDark) Color(0xFF16213E) else Color(0xFFE8E8EE)

    val text: Color
        get() = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1A1A)
    val textDim: Color
        get() = if (isDark) Color(0xB3FFFFFF) else Color(0xB3000000)
    val textFaint: Color
        get() = if (isDark) Color(0x80FFFFFF) else Color(0x80000000)

    val cardBg: Color
        get() = if (isDark) Color(0x0FFFFFFF) else Color(0x08000000)
    val cardBgStrong: Color
        get() = if (isDark) Color(0x14FFFFFF) else Color(0x10000000)
    val border: Color
        get() = if (isDark) Color(0x1FFFFFFF) else Color(0x1F000000)
    val glassTint: Color
        get() = if (isDark) Color(0x1AFFFFFF) else Color(0x1A000000)
    val iconBg: Color
        get() = if (isDark) Color(0x1AFFFFFF) else Color(0x0D000000)

    fun setAccent(color: Color) {
        accent = color
        accentValue = color.value.toLong()
    }

    fun setAccent(id: String, value: Long) {
        presetId = id
        accentValue = value
        accent = Color(value)
    }
}