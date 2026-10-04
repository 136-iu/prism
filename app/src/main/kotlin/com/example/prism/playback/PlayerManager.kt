package com.example.prism.playback

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.prism.data.Song
import com.example.prism.data.Storage
import com.example.prism.widget.WidgetDataCache
import com.example.prism.widget.WidgetUpdateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object PlayerManager {

    private var player: ExoPlayer? = null
    private var appContext: Context? = null
    private var tickJob: Job? = null
    private var serviceStarted = false
    private var lastWidgetUpdate = 0L

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist

    private val _progressMs = MutableStateFlow(0L)
    val progressMs: StateFlow<Long> = _progressMs

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs

    private val _shuffle = MutableStateFlow(false)
    val shuffle: StateFlow<Boolean> = _shuffle

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    fun init(context: Context) {
        if (player != null) return
        appContext = context.applicationContext
        player = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    updateWidgets(force = true)
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val idx = currentMediaItemIndex
                    val list = _playlist.value
                    if (idx in list.indices) {
                        _currentSong.value = list[idx]
                        recordPlay(list[idx])
                    }
                    updateWidgets(force = true)
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        _durationMs.value = duration.coerceAtLeast(0)
                    }
                }
                override fun onShuffleModeEnabledChanged(enabled: Boolean) {
                    _shuffle.value = enabled
                }
                override fun onRepeatModeChanged(mode: Int) {
                    _repeatMode.value = mode
                }
            })
        }
        startTick()
    }

    private fun startForegroundService() {
        if (serviceStarted) return
        val ctx = appContext ?: return
        try {
            val intent = Intent(ctx, PlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(intent)
            else ctx.startService(intent)
            serviceStarted = true
        } catch (_: Exception) {}
    }

    private fun startTick() {
        tickJob?.cancel()
        tickJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(1000)
                val p = player ?: continue
                if (p.isPlaying) {
                    _progressMs.value = p.currentPosition
                }
                val d = p.duration
                if (d > 0 && _durationMs.value != d) {
                    _durationMs.value = d
                }
            }
        }
    }

    private fun updateWidgets(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && now - lastWidgetUpdate < 5000L) return
        lastWidgetUpdate = now
        appContext?.let {
            WidgetDataCache.currentSong = _currentSong.value
            WidgetDataCache.isPlaying = _isPlaying.value
            WidgetDataCache.positionMs = _progressMs.value
            WidgetDataCache.durationMs = _durationMs.value
            try { WidgetUpdateHelper.updateAll(it) } catch (_: Exception) {}
        }
    }

    private fun recordPlay(song: Song) {
        val history = Storage.loadHistory().toMutableList()
        history.remove(song.id)
        history.add(0, song.id)
        Storage.saveHistory(history.take(1000))

        val stats = Storage.loadStats().toMutableList()
        val idx = stats.indexOfFirst { it.songId == song.id }
        if (idx >= 0) {
            val s = stats[idx]
            stats[idx] = s.copy(
                playCount = s.playCount + 1,
                lastPlayedAt = System.currentTimeMillis()
            )
        } else {
            stats.add(Storage.Stats(song.id, 0L, 1, System.currentTimeMillis()))
        }
        Storage.saveStats(stats)
    }

    fun setAllSongs(songs: List<Song>) { _allSongs.value = songs }

    fun playSong(song: Song, list: List<Song> = _allSongs.value) {
        val p = player ?: return
        if (list.isEmpty()) return

        startForegroundService()
        _playlist.value = list
        p.clearMediaItems()
        list.forEach { s ->
            p.addMediaItem(
                MediaItem.Builder()
                    .setUri(s.uri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(s.title)
                            .setArtist(s.artist)
                            .setAlbumTitle(s.album)
                            .setArtworkUri(s.artworkUri)
                            .build()
                    )
                    .build()
            )
        }
        val index = list.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        p.seekTo(index, 0L)
        p.prepare()
        p.play()
        _currentSong.value = song
        _progressMs.value = 0L
        updateWidgets(force = true)
    }

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) p.pause() else p.play()
        updateWidgets(force = true)
    }

    fun next() { player?.seekToNextMediaItem(); player?.play() }
    fun previous() { player?.seekToPreviousMediaItem(); player?.play() }
    fun seekTo(ms: Long) { player?.seekTo(ms); _progressMs.value = ms }

    fun toggleShuffle() {
        val p = player ?: return
        p.shuffleModeEnabled = !p.shuffleModeEnabled
    }

    fun cycleRepeatMode() {
        val p = player ?: return
        p.repeatMode = when (p.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun updateProgress() {
        _progressMs.value = player?.currentPosition ?: 0L
    }

    fun getPlayer(): ExoPlayer? = player

    fun release() {
        tickJob?.cancel()
        player?.release()
        player = null
    }
}