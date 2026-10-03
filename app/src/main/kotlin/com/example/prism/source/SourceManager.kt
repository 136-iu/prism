package com.example.prism.source

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 在线歌曲
 */
data class OnlineSong(
    val sourceId: String,        // 来自哪个音源
    val id: String,
    val name: String,
    val artist: String,
    val album: String,
    val duration: Int,           // 秒
    val cover: String?
)

object SourceManager {

    /**
     * 导入音源脚本（把文件内容存到应用私有目录）
     */
    suspend fun import(
        context: Context,
        scriptContent: String,
        originalName: String
    ): SourceInfo? = withContext(Dispatchers.IO) {
        try {
            val meta = SourceEngine.readMetaFromContent(context, scriptContent)
                ?: SourceMeta(originalName.removeSuffix(".js"), "1.0", "未知", "")

            val id = "src_${System.currentTimeMillis()}"
            val dir = java.io.File(context.filesDir, "sources")
            if (!dir.exists()) dir.mkdirs()
            val file = java.io.File(dir, "$id.js")
            file.writeText(scriptContent)

            val info = SourceInfo(
                id = id,
                name = meta.name,
                version = meta.version,
                author = meta.author,
                description = meta.description,
                scriptPath = file.absolutePath
            )
            SourceStore.add(info)
            info
        } catch (_: Exception) { null }
    }

    /**
     * 搜索所有启用的音源（并行）
     */
    suspend fun searchAll(context: Context, keyword: String): List<OnlineSong> =
        coroutineScope {
            val sources = SourceStore.load().filter { it.enabled }
            val tasks = sources.map { src ->
                async(Dispatchers.IO) {
                    try {
                        val json = SourceEngine.executeSearch(src.scriptPath, keyword) ?: return@async emptyList()
                        parseSearchResult(src.id, json)
                    } catch (_: Exception) { emptyList() }
                }
            }
            tasks.awaitAll().flatten()
        }

    /**
     * 获取播放地址（按音源顺序尝试）
     */
    suspend fun resolveUrl(context: Context, sourceId: String, songId: String): Pair<String, Map<String, String>>? =
        withContext(Dispatchers.IO) {
            val src = SourceStore.load().firstOrNull { it.id == sourceId } ?: return@withContext null
            try {
                val json = SourceEngine.executeGetUrl(src.scriptPath, songId) ?: return@withContext null
                val obj = JSONObject(json)
                val url = obj.optString("url", "")
                if (url.isBlank()) return@withContext null

                val headers = mutableMapOf<String, String>()
                obj.optJSONObject("headers")?.let { h ->
                    h.keys().forEach { k -> headers[k] = h.optString(k, "") }
                }
                url to headers
            } catch (_: Exception) { null }
        }

    /**
     * 获取歌词
     */
    suspend fun resolveLyrics(context: Context, sourceId: String, songId: String): String? =
        withContext(Dispatchers.IO) {
            val src = SourceStore.load().firstOrNull { it.id == sourceId } ?: return@withContext null
            try {
                val json = SourceEngine.executeGetLyrics(src.scriptPath, songId) ?: return@withContext null
                val obj = JSONObject(json)
                obj.optString("lyric", null) ?: obj.optString("lyrics", null)
            } catch (_: Exception) { null }
        }

    /**
     * 获取榜单（给主页在线模式用）
     */
    suspend fun getHotList(context: Context): List<OnlineSong> = coroutineScope {
        val sources = SourceStore.load().filter { it.enabled }
        val tasks = sources.map { src ->
            async(Dispatchers.IO) {
                try {
                    val json = SourceEngine.executeGetHotList(src.scriptPath) ?: return@async emptyList()
                    parseSearchResult(src.id, json)
                } catch (_: Exception) { emptyList() }
            }
        }
        tasks.awaitAll().flatten()
    }

    private fun parseSearchResult(sourceId: String, json: String): List<OnlineSong> {
        return try {
            val obj = JSONObject(json)
            val arr = obj.optJSONArray("list")
                ?: obj.optJSONArray("songs")
                ?: return emptyList()
            val result = mutableListOf<OnlineSong>()
            for (i in 0 until arr.length()) {
                val s = arr.getJSONObject(i)
                result.add(
                    OnlineSong(
                        sourceId = sourceId,
                        id = s.optString("id", s.optString("songmid", "")),
                        name = s.optString("name", s.optString("title", "")),
                        artist = s.optString("singer", s.optString("artist", "")),
                        album = s.optString("album", ""),
                        duration = s.optInt("duration", s.optInt("interval", 0)),
                        cover = s.optString("cover", s.optString("pic", null))
                    )
                )
            }
            result
        } catch (_: Exception) { emptyList() }
    }
}

/**
 * 从脚本内容里读元信息（不落盘）
 */
private fun SourceEngine.readMetaFromContent(context: Context, content: String): SourceMeta? {
    return try {
        val dir = java.io.File(context.cacheDir, "tmp_src")
        if (!dir.exists()) dir.mkdirs()
        val tmp = java.io.File(dir, "check.js")
        tmp.writeText(content)
        val result = readMeta(tmp.absolutePath)
        tmp.delete()
        result
    } catch (_: Exception) { null }
}