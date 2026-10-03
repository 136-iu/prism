package com.example.prism.util

import android.content.Context
import com.example.prism.data.Song

/**
 * 歌词加载入口
 * 综合本地文件 + 缓存
 */
object LyricsStore {

    fun load(context: Context, song: Song): String? {
        // 1. 查缓存
        LyricsFetcher.loadCache(context, song.id)?.let { return it }

        // 2. 查本地 .lrc
        loadLocalFile(song)?.let {
            LyricsFetcher.saveCache(context, song.id, it)
            return it
        }

        return null
    }

    private fun loadLocalFile(song: Song): String? {
        if (song.path.isBlank()) return null
        try {
            val audio = java.io.File(song.path)
            val dir = audio.parentFile ?: return null
            val base = audio.nameWithoutExtension
            val lrc = java.io.File(dir, "$base.lrc")
            if (lrc.exists() && lrc.canRead()) return lrc.readText()
            val txt = java.io.File(dir, "$base.txt")
            if (txt.exists() && txt.canRead()) return txt.readText()
        } catch (_: Exception) {}
        return null
    }
}