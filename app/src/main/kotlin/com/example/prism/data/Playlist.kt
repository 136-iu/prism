package com.example.prism.data

data class Playlist(
    val id: String,
    val name: String,
    val songIds: List<Long>,
    val coverUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)