package com.example.prism.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object AiManager {

    /**
     * 歌词翻译（分批，每批 10 行）
     */
    suspend fun translateLyrics(
        context: Context,
        lyricsText: String,
        targetLang: String = "中文"
    ): String? = withContext(Dispatchers.IO) {
        val provider = AiStore.getProviderFor("lyric_translation") ?: return@withContext null
        val lines = lyricsText.lines()
        val result = StringBuilder()
        val batches = lines.chunked(10)
        for (batch in batches) {
            val prompt = "把下面这些歌词逐行翻译成$targetLang，" +
                    "只输出翻译结果，每行对应一行，不要加任何解释：\n\n" +
                    batch.joinToString("\n")
            val translated = chatInternal(provider, prompt, systemPrompt = null) ?: continue
            result.append(translated).append("\n")
        }
        result.toString().trim()
    }

    /**
     * 通用对话
     */
    suspend fun chat(context: Context, prompt: String, task: String? = null): String? =
        withContext(Dispatchers.IO) {
            val provider = if (task != null) AiStore.getProviderFor(task)
                           else AiStore.loadProviders().firstOrNull { it.enabled }
            provider ?: return@withContext null
            chatInternal(provider, prompt, null)
        }

    /**
     * 智能歌单生成
     */
    suspend fun generatePlaylist(
        context: Context,
        allSongs: List<String>,
        userRequest: String
    ): List<String>? = withContext(Dispatchers.IO) {
        val provider = AiStore.getProviderFor("playlist_generation") ?: return@withContext null
        val prompt = """
            用户想听：$userRequest

            从下面歌曲列表里选出最合适的 10~20 首，只输出歌名，每行一首：
            
            ${allSongs.joinToString("\n")}
        """.trimIndent()
        val result = chatInternal(provider, prompt, null) ?: return@withContext null
        result.lines().map { it.trim() }.filter { it.isNotEmpty() }
    }

    /**
     * 歌词解释
     */
    suspend fun explainLyrics(context: Context, songTitle: String, artist: String, lyrics: String): String? =
        withContext(Dispatchers.IO) {
            val provider = AiStore.getProviderFor("lyric_explanation") ?: return@withContext null
            val prompt = "请简要解读这首歌词《$songTitle》- $artist 的含义、背景和写作特点：\n\n$lyrics"
            chatInternal(provider, prompt, null)
        }

    /**
     * 情绪分析
     */
    suspend fun analyzeMood(context: Context, lyrics: String): String? =
        withContext(Dispatchers.IO) {
            val provider = AiStore.getProviderFor("mood_analysis") ?: return@withContext null
            val prompt = "分析下面歌词的情绪（欢快/悲伤/平静/激动/忧郁 等），一句话概括：\n\n$lyrics"
            chatInternal(provider, prompt, null)
        }

    /**
     * 歌曲解说 / 音乐故事
     */
    suspend fun describeSong(context: Context, songTitle: String, artist: String): String? =
        withContext(Dispatchers.IO) {
            val provider = AiStore.getProviderFor("song_commentary") ?: return@withContext null
            val prompt = "介绍《$songTitle》- $artist 的创作背景、歌手信息和有趣的故事。"
            chatInternal(provider, prompt, null)
        }

    /**
     * 封面匹配（根据歌名歌手搜封面 URL）
     */
    suspend fun matchCover(context: Context, songTitle: String, artist: String): String? =
        withContext(Dispatchers.IO) {
            val provider = AiStore.getProviderFor("cover_matching") ?: return@withContext null
            val prompt = "搜索《$songTitle》- $artist 的专辑封面图片 URL，" +
                    "只输出一个直链图片地址，不要任何其他内容。"
            chatInternal(provider, prompt, null)
        }

    /**
     * 内部：调用 OpenAI 兼容接口
     */
    private fun chatInternal(
        provider: AiProvider,
        userPrompt: String,
        systemPrompt: String?
    ): String? {
        return try {
            val url = URL("${provider.baseUrl.trimEnd('/')}/chat/completions")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer ${provider.apiKey}")
            conn.connectTimeout = 30000
            conn.readTimeout = 90000
            conn.doOutput = true

            val messages = JSONArray()
            if (systemPrompt != null) {
                messages.put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
            }
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", userPrompt)
            })

            val body = JSONObject().apply {
                put("model", provider.model)
                put("messages", messages)
                put("temperature", 0.3)
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body.toString()) }

            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().use { it.readText() }
            conn.disconnect()

            try {
                val root = JSONObject(text)
                val choices = root.optJSONArray("choices") ?: return null
                val first = choices.optJSONObject(0) ?: return null
                val msg = first.optJSONObject("message") ?: return null
                msg.optString("content", null)
            } catch (_: Exception) { null }
        } catch (_: Exception) { null }
    }
}