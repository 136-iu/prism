package com.example.prism.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Storage {

    private const val PREFS = "prism_storage"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private const val KEY_PLAYLISTS = "playlists"
    private const val KEY_HISTORY = "history"
    private const val KEY_STATS = "stats"
    private const val KEY_THEME_DARK = "theme_dark"
    private const val KEY_ACCENT_ID = "accent_id"
    private const val KEY_ACCENT_VALUE = "accent_value"
    private const val KEY_GLASS_ENABLED = "glass_enabled"
    private const val KEY_GLASS_INTENSITY = "glass_intensity"
    private const val KEY_PLAYER_STYLE = "player_style"
    private const val KEY_PLAYER_STYLE_CHOSEN = "player_style_chosen"
    private const val KEY_FONT_FOLLOW_SYSTEM = "font_follow_system"
    private const val KEY_ANIM_INTENSITY = "anim_intensity"
    private const val KEY_ANIM_SQUASH = "anim_squash"
    private const val KEY_ANIM_STAGGER = "anim_stagger"
    private const val KEY_ANIM_RIPPLE = "anim_ripple"
    private const val KEY_ANIM_SHAKE = "anim_shake"
    private const val KEY_ANIM_BREATHE = "anim_breathe"
    private const val KEY_LAB_STAGGERED = "lab_staggered"
    private const val KEY_LAB_3D = "lab_3d"
    private const val KEY_LAB_AUTO_PLAYER = "lab_auto_player"
    private const val KEY_LAB_FOLDER_COVER = "lab_folder_cover"
    private const val KEY_HOME_BIG_SIDE = "home_big_side"
    private const val KEY_HOME_SMALL_COLS = "home_small_cols"
    private const val KEY_HOME_SMALL_ROWS = "home_small_rows"
    private const val KEY_DEFAULT_ONLINE = "default_online"
    private const val KEY_WIDGET_COVER = "widget_cover"
    private const val KEY_WIDGET_PROGRESS = "widget_progress"
    private const val KEY_WIDGET_FOLLOW = "widget_follow"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    // ---------- 通用 ----------
    private inline fun <reified T> loadList(key: String): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson<List<T>>(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    private inline fun <reified T> saveList(key: String, list: List<T>) {
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }

    // ---------- 歌单 ----------
    fun loadPlaylists(): List<Playlist> = loadList(KEY_PLAYLISTS)
    fun savePlaylists(list: List<Playlist>) = saveList(KEY_PLAYLISTS, list)

    // ---------- 播放历史 ----------
    fun loadHistory(): List<Long> = loadList(KEY_HISTORY)
    fun saveHistory(list: List<Long>) = saveList(KEY_HISTORY, list)

    // ---------- 听歌统计 ----------
    data class Stats(
        val songId: Long,
        val totalDuration: Long,
        val playCount: Int,
        val lastPlayedAt: Long
    )
    fun loadStats(): List<Stats> = loadList(KEY_STATS)
    fun saveStats(list: List<Stats>) = saveList(KEY_STATS, list)

    // ---------- 主题 ----------
    fun isDark(): Boolean = prefs.getBoolean(KEY_THEME_DARK, true)
    fun setDark(dark: Boolean) { prefs.edit().putBoolean(KEY_THEME_DARK, dark).apply() }

    fun getAccentId(): String = prefs.getString(KEY_ACCENT_ID, "purple") ?: "purple"
    fun getAccentValue(): Long = prefs.getLong(KEY_ACCENT_VALUE, 0xFF7C4DFF)
    fun setAccent(id: String, value: Long) {
        prefs.edit().putString(KEY_ACCENT_ID, id).putLong(KEY_ACCENT_VALUE, value).apply()
    }

    fun isGlassEnabled(): Boolean = prefs.getBoolean(KEY_GLASS_ENABLED, true)
    fun setGlassEnabled(v: Boolean) { prefs.edit().putBoolean(KEY_GLASS_ENABLED, v).apply() }

    fun getGlassIntensity(): Float = prefs.getFloat(KEY_GLASS_INTENSITY, 0.55f)
    fun setGlassIntensity(v: Float) { prefs.edit().putFloat(KEY_GLASS_INTENSITY, v).apply() }

    fun getPlayerStyle(): String = prefs.getString(KEY_PLAYER_STYLE, "IOS26") ?: "IOS26"
    fun setPlayerStyle(v: String) { prefs.edit().putString(KEY_PLAYER_STYLE, v).apply() }

    fun isPlayerStyleChosen(): Boolean = prefs.getBoolean(KEY_PLAYER_STYLE_CHOSEN, false)
    fun setPlayerStyleChosen(v: Boolean) { prefs.edit().putBoolean(KEY_PLAYER_STYLE_CHOSEN, v).apply() }

    // ---------- 字体 ----------
    fun isFontFollowSystem(): Boolean = prefs.getBoolean(KEY_FONT_FOLLOW_SYSTEM, true)
    fun setFontFollowSystem(v: Boolean) { prefs.edit().putBoolean(KEY_FONT_FOLLOW_SYSTEM, v).apply() }

    // ---------- 动画 ----------
    fun getAnimIntensity(): Float = prefs.getFloat(KEY_ANIM_INTENSITY, 0.5f)
    fun setAnimIntensity(v: Float) { prefs.edit().putFloat(KEY_ANIM_INTENSITY, v).apply() }

    fun getAnimFlag(key: String, def: Boolean): Boolean = prefs.getBoolean(key, def)
    fun setAnimFlag(key: String, v: Boolean) { prefs.edit().putBoolean(key, v).apply() }

    val animSquashKey = KEY_ANIM_SQUASH
    val animStaggerKey = KEY_ANIM_STAGGER
    val animRippleKey = KEY_ANIM_RIPPLE
    val animShakeKey = KEY_ANIM_SHAKE
    val animBreatheKey = KEY_ANIM_BREATHE

    // ---------- 实验室 ----------
    fun getLabFlag(key: String, def: Boolean = false): Boolean = prefs.getBoolean(key, def)
    fun setLabFlag(key: String, v: Boolean) { prefs.edit().putBoolean(key, v).apply() }

    val labStaggeredKey = KEY_LAB_STAGGERED
    val lab3DKey = KEY_LAB_3D
    val labAutoPlayerKey = KEY_LAB_AUTO_PLAYER
    val labFolderCoverKey = KEY_LAB_FOLDER_COVER

    // ---------- 主页设置 ----------
    fun getHomeBigSide(): String = prefs.getString(KEY_HOME_BIG_SIDE, "left") ?: "left"
    fun setHomeBigSide(v: String) { prefs.edit().putString(KEY_HOME_BIG_SIDE, v).apply() }

    fun getHomeSmallCols(): Int = prefs.getInt(KEY_HOME_SMALL_COLS, 4)
    fun setHomeSmallCols(v: Int) { prefs.edit().putInt(KEY_HOME_SMALL_COLS, v).apply() }

    fun getHomeSmallRows(): Int = prefs.getInt(KEY_HOME_SMALL_ROWS, 6)
    fun setHomeSmallRows(v: Int) { prefs.edit().putInt(KEY_HOME_SMALL_ROWS, v).apply() }

    fun getDefaultOnline(): Boolean = prefs.getBoolean(KEY_DEFAULT_ONLINE, true)
    fun setDefaultOnline(v: Boolean) { prefs.edit().putBoolean(KEY_DEFAULT_ONLINE, v).apply() }

    // ---------- 小组件 ----------
    fun getWidgetCover(): Boolean = prefs.getBoolean(KEY_WIDGET_COVER, true)
    fun setWidgetCover(v: Boolean) { prefs.edit().putBoolean(KEY_WIDGET_COVER, v).apply() }

    fun getWidgetProgress(): Boolean = prefs.getBoolean(KEY_WIDGET_PROGRESS, true)
    fun setWidgetProgress(v: Boolean) { prefs.edit().putBoolean(KEY_WIDGET_PROGRESS, v).apply() }

    fun getWidgetFollow(): Boolean = prefs.getBoolean(KEY_WIDGET_FOLLOW, true)
    fun setWidgetFollow(v: Boolean) { prefs.edit().putBoolean(KEY_WIDGET_FOLLOW, v).apply() }

    // ---------- 首次启动 ----------
    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    fun markLaunched() { prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply() }
}