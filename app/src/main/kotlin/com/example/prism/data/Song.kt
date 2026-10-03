package com.example.prism.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,       // 毫秒
    val uri: Uri,
    val albumId: Long,
    val artworkUri: Uri?,
    val path: String,
    val dateAdded: Long,
    val size: Long
) {
    fun durationText(): String {
        val sec = duration / 1000
        return "%d:%02d".format(sec / 60, sec % 60)
    }
}