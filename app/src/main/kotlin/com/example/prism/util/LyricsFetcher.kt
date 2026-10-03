package com.example.prism.util

import android.content.Context
import com.example.prism.data.Song
import com.example.prism.data.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * 歌词自动搜索
 * 优先级：本地文件 → LRCLIB API → 缓存
 */
object LyricsFetcher {

    suspend fun fetch(context: Context, song: Song): String? = withContext(Dispatchers.IO) {
        // 1. 查缓存
        val cached = loadCache(context, song.id)
        if (cached != null) return@withContext cached

        // 2. 查本地同目录 .lrc
        val local = loadLocalFile(song)
        if (local != null) {
            saveCache(context, song.id, local)
            return@withContext local
        }

        // 3. 联网查 LRCLIB
        val online = fetchLrclib(song)
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
            val lrc = File(dir, "$base.lrc")
            if (lrc.exists() && lrc.canRead()) return lrc.readText()
            val txt = File(dir, "$base.txt")
            if (txt.exists() && txt.canRead()) return txt.readText()
        } catch (_: Exception) {}
        return null
    }

    private suspend fun fetchLrclib(song: Song): String? {
        return try {
            val title = URLEncoder.encode(song.title, "UTF-8")
            val artist = URLEncoder.encode(song.artist, "UTF-8")
            val album = URLEncoder.encode(song.album, "UTF-8")
            val dur = (song.duration / 1000).toString()

            val url = URL(
                "https://lrclib.net/api/get?" +
                "artist_name=$artist&track_name=$title&album_name=$album&duration=$dur"
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.setRequestProperty("User-Agent", "Prism/1.0")
            if (conn.responseCode !in 200..299) {
                conn.disconnect()
                return null
            }
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val root = JSONObject(text)
            val synced = root.optString("syncedLyrics", "")
            val plain = root.optString("plainLyrics", "")
            when {
                synced.isNotBlank() -> synced
                plain.isNotBlank() -> convertPlain(plain)
                else -> null
            }
        } catch (_: Exception) { null }
    }

    private fun convertPlain(plain: String): String {
        val sb = StringBuilder()
        var i = 0
        plain.lines().forEach { line ->
            sb.append("[%02d:%02d.00]%s\n".format(i / 60, i % 60, line))
            i += 4
        }
        return sb.toString()
    }

    // ---------- 缓存 ----------
    private fun cacheFile(context: Context, songId: Long): File {
        val dir = File(context.filesDir, "lyrics")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$songId.lrc")
    }

    fun loadCache(context: Context, songId: Long): String? {
        val f = cacheFile(context, songId)
        if (!f.exists()) return null
        return try { f.readText() } catch (_: Exception) { null }
    }

    fun saveCache(context: Context, songId: Long, lrc: String) {
        try { cacheFile(context, songId).writeText(lrc) } catch (_: Exception) {}
    }
}