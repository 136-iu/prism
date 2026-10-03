package com.example.prism.util

data class LyricLine(
    val timeMs: Long,
    val text: String
)

object LyricsParser {

    private val regex = Regex("""\[(\d{2}):(\d{2})[.:](\d{2,3})](.*)""")

    /**
     * 解析 LRC 格式歌词
     * 支持一行多个时间标签：[00:12.34][00:15.67]歌词内容
     */
    fun parse(lrc: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        lrc.lines().forEach { raw ->
            val matches = regex.findAll(raw).toList()
            if (matches.isEmpty()) return@forEach
            val text = matches.last().groupValues[4].trim()
            if (text.isEmpty()) return@forEach
            matches.forEach { m ->
                val min = m.groupValues[1].toLongOrNull() ?: 0
                val sec = m.groupValues[2].toLongOrNull() ?: 0
                val msRaw = m.groupValues[3].toLongOrNull() ?: 0
                val ms = if (msRaw < 100) msRaw * 10 else msRaw
                lines.add(LyricLine(min * 60_000 + sec * 1_000 + ms, text))
            }
        }
        return lines.sortedBy { it.timeMs }
    }

    fun findCurrentIndex(lines: List<LyricLine>, positionMs: Long): Int {
        if (lines.isEmpty()) return -1
        for (i in lines.indices.reversed()) {
            if (lines[i].timeMs <= positionMs) return i
        }
        return 0
    }

    /**
     * 检测是否以英文为主
     */
    fun isMostlyEnglish(text: String): Boolean {
        if (text.isBlank()) return false
        var en = 0
        var zh = 0
        text.forEach { c ->
            when {
                c in 'a'..'z' || c in 'A'..'Z' -> en++
                c in '\u4e00'..'\u9fff' -> zh++
            }
        }
        return en > zh * 2 && en > 20
    }

    /**
     * 估算当前行的进度（0~1），用于逐字动画
     */
    fun progressInLine(lines: List<LyricLine>, index: Int, positionMs: Long): Float {
        if (index < 0 || index >= lines.size) return 0f
        val start = lines[index].timeMs
        val end = if (index + 1 < lines.size) lines[index + 1].timeMs
                  else start + 3000L
        val total = (end - start).coerceAtLeast(1)
        return ((positionMs - start).toFloat() / total).coerceIn(0f, 1f)
    }
}