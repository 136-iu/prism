package com.example.prism.util

import android.content.Context
import com.example.prism.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * 封面自动搜索
 * 优先级：同文件夹同名图片 → iTunes Search API → 缓存
 */
object CoverFetcher {

    suspend fun fetch(context: Context, song: Song): String? = withContext(Dispatchers.IO) {
        // 1. 查缓存
        val cached = loadCache(context, song.id)
        if (cached != null) return@withContext cached

        // 2. 查同文件夹
        val local = loadLocalFile(song)
        if (local != null) {
            saveCache(context, song.id, local)
            return@withContext local
        }

        // 3. 联网查 iTunes
        val online = fetchItunes(song)
        if (online != null) {
            saveCache(context, song.id, online)
            return@withContext online
        }

        null
    }

    private fun loadLocalFile(song: Song): String? {
        if (song.path.isBlank()) return null
        try {
            val audio = File(song.path)
            val dir = audio.parentFile ?: return null
            val base = audio.nameWithoutExtension
            listOf("jpg", "jpeg", "png", "webp").forEach { ext ->
                val f = File(dir, "$base.$ext")
                if (f.exists() && f.canRead()) return f.absolutePath
            }
            // 同目录 cover / folder / album
            listOf("cover", "folder", "album", "front").forEach { name ->
                listOf("jpg", "jpeg", "png", "webp").forEach { ext ->
                    val f = File(dir, "$name.$ext")
                    if (f.exists() && f.canRead()) return f.absolutePath
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private suspend fun fetchItunes(song: Song): String? {
        return try {
            val term = URLEncoder.encode("${song.artist} ${song.title}", "UTF-8")
            val url = URL("https://itunes.apple.com/search?term=$term&media=music&limit=1")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val root = JSONObject(text)
            val results = root.optJSONArray("results") ?: return null
            if (results.length() == 0) return null
            val first = results.getJSONObject(0)
            first.optString("artworkUrl100", null)
                ?.replace("100x100", "600x600")
        } catch (_: Exception) { null }
    }

    // ---------- 缓存 ----------
    private fun cacheFile(context: Context, songId: Long): File {
        val dir = File(context.filesDir, "covers")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$songId.txt")
    }

    fun loadCache(context: Context, songId: Long): String? {
        val f = cacheFile(context, songId)
        if (!f.exists()) return null
        return try { f.readText() } catch (_: Exception) { null }
    }

    fun saveCache(context: Context, songId: Long, urlOrPath: String) {
        try { cacheFile(context, songId).writeText(urlOrPath) } catch (_: Exception) {}
    }
}