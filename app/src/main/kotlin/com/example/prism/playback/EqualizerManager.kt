package com.example.prism.playback

import android.media.audiofx.Equalizer
import com.example.prism.data.BuiltInEqPresets
import com.example.prism.data.EqPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object EqualizerManager {

    private var equalizer: Equalizer? = null
    private var numberOfBands: Short = 0
    private var minLevel: Short = -1500
    private var maxLevel: Short = 1500

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled

    private val _bandLevels = MutableStateFlow<List<Float>>(List(10) { 0f })
    val bandLevels: StateFlow<List<Float>> = _bandLevels

    private val _preamp = MutableStateFlow(0f)
    val preamp: StateFlow<Float> = _preamp

    private val _currentPresetId = MutableStateFlow("flat")
    val currentPresetId: StateFlow<String> = _currentPresetId

    fun init(audioSessionId: Int) {
        try {
            equalizer?.release()
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = false
            }
            numberOfBands = equalizer?.numberOfBands ?: 0
            val range = equalizer?.bandLevelRange
            if (range != null) {
                minLevel = range[0]
                maxLevel = range[1]
            }
            applyPreset(BuiltInEqPresets.all.first(), silent = true)
        } catch (_: Exception) {
            equalizer = null
        }
    }

    fun setEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
            _enabled.value = enabled
        } catch (_: Exception) {}
    }

    fun setBandLevel(band: Int, levelDb: Float) {
        try {
            val bandShort = band.toShort()
            if (bandShort in 0 until numberOfBands) {
                val level = (levelDb * 100).toInt()
                    .coerceIn(minLevel.toInt(), maxLevel.toInt())
                    .toShort()
                equalizer?.setBandLevel(bandShort, level)
                val list = _bandLevels.value.toMutableList()
                if (band < list.size) list[band] = levelDb
                _bandLevels.value = list
                _currentPresetId.value = "custom"
            }
        } catch (_: Exception) {}
    }

    fun applyPreset(preset: EqPreset, silent: Boolean = false) {
        try {
            preset.bands.forEachIndexed { i, db -> setBandLevel(i, db) }
            _preamp.value = preset.preamp
            _currentPresetId.value = preset.id
            if (!silent) setEnabled(true)
        } catch (_: Exception) {}
    }

    fun getBandCount(): Int = numberOfBands.toInt().coerceAtLeast(10)

    fun getLevelRangeDb(): ClosedFloatingPointRange<Float> {
        return (minLevel / 100f)..(maxLevel / 100f)
    }

    fun release() {
        try {
            equalizer?.release()
        } catch (_: Exception) {}
        equalizer = null
    }
}