package com.example.prism.data

object PlaylistStore {

    fun load(): List<Playlist> = Storage.loadPlaylists()

    fun save(list: List<Playlist>) = Storage.savePlaylists(list)

    fun create(name: String): Playlist {
        val playlist = Playlist(
            id = "pl_${System.currentTimeMillis()}",
            name = name,
            songIds = emptyList()
        )
        val list = load().toMutableList()
        list.add(playlist)
        save(list)
        return playlist
    }

    fun delete(id: String) {
        save(load().filter { it.id != id })
    }

    fun rename(id: String, newName: String) {
        save(load().map {
            if (it.id == id) it.copy(name = newName, updatedAt = System.currentTimeMillis())
            else it
        })
    }

    fun addSong(id: String, songId: Long) {
        save(load().map {
            if (it.id == id && songId !in it.songIds) {
                it.copy(songIds = it.songIds + songId, updatedAt = System.currentTimeMillis())
            } else it
        })
    }

    fun removeSong(id: String, songId: Long) {
        save(load().map {
            if (it.id == id) it.copy(
                songIds = it.songIds.filter { sid -> sid != songId },
                updatedAt = System.currentTimeMillis()
            )
            else it
        })
    }
}