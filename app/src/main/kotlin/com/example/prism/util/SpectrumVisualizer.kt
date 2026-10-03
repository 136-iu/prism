package com.example.prism.util

import android.media.audiofx.Visualizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 用系统 Visualizer 采集波形和 FFT 数据
 */
object SpectrumVisualizer {

    private var visualizer: Visualizer? = null

    private val _waveform = MutableStateFlow<List<Float>>(emptyList())
    val waveform: StateFlow<List<Float>> = _waveform

    private val _fft = MutableStateFlow<List<Float>>(emptyList())
    val fft: StateFlow<List<Float>> = _fft

    fun init(audioSessionId: Int) {
        try {
            release()
            visualizer = Visualizer(audioSessionId).apply {
                val range = Visualizer.getCaptureSizeRange()
                captureSize = range[0].coerceAtLeast(128)
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            v: Visualizer?, waveform: ByteArray?, samplingRate: Int
                        ) {
                            val list = waveform?.map {
                                (it.toInt() and 0xFF) / 128f - 1f
                            } ?: emptyList()
                            _waveform.value = list
                        }

                        override fun onFftDataCapture(
                            v: Visualizer?, fft: ByteArray?, samplingRate: Int
                        ) {
                            val list = fft?.map {
                                ((it.toInt() and 0xFF) / 128f).coerceIn(0f, 1f)
                            } ?: emptyList()
                            _fft.value = list
                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true, true
                )
                enabled = true
            }
        } catch (_: Exception) {
            visualizer = null
        }
    }

    fun release() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (_: Exception) {}
        visualizer = null
        _waveform.value = emptyList()
        _fft.value = emptyList()
    }
}