package com.example.prism.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object SleepTimer {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds

    private val _minutes = MutableStateFlow(0)
    val minutes: StateFlow<Int> = _minutes

    fun start(min: Int) {
        cancel()
        _minutes.value = min
        _remainingSeconds.value = min * 60L
        job = scope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            PlayerManager.togglePlayPause()
            _remainingSeconds.value = 0
            _minutes.value = 0
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        _remainingSeconds.value = 0
        _minutes.value = 0
    }

    fun isRunning(): Boolean = job?.isActive == true
}