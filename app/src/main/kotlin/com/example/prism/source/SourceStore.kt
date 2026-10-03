package com.example.prism.source

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 音源信息
 */
data class SourceInfo(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val scriptPath: String,      // 脚本文件路径（应用私有目录）
    val enabled: Boolean = true,
    val rating: Int = 0,          // 用户评分 0~5
    val importedAt: Long = System.currentTimeMillis()
)

object SourceStore {

    private const val PREFS = "prism_sources"
    private const val KEY_LIST = "sources"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun load(): List<SourceInfo> {
        val json = prefs.getString(KEY_LIST, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<SourceInfo>>() {}.type
            gson.fromJson<List<SourceInfo>>(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    fun save(list: List<SourceInfo>) {
        prefs.edit().putString(KEY_LIST, gson.toJson(list)).apply()
    }

    fun add(source: SourceInfo) {
        val list = load().toMutableList()
        list.removeAll { it.id == source.id }
        list.add(source)
        save(list)
    }

    fun remove(id: String) {
        save(load().filter { it.id != id })
    }

    fun toggle(id: String) {
        save(load().map { if (it.id == id) it.copy(enabled = !it.enabled) else it })
    }

    fun rate(id: String, rating: Int) {
        save(load().map { if (it.id == id) it.copy(rating = rating.coerceIn(0, 5)) else it })
    }
}