package com.example.prism.widget

import com.example.prism.data.Song

/**
 * 小组件和播放器的共享数据缓存
 * 播放状态变化时更新，小组件读取
 */
object WidgetDataCache {
    var currentSong: Song? = null
    var isPlaying: Boolean = false
    var positionMs: Long = 0L
    var durationMs: Long = 0L
}