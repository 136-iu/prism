package com.example.prism

import android.app.Application
import android.content.Context
import com.example.prism.ai.AiStore
import com.example.prism.data.Storage
import com.example.prism.notification.MediaNotificationManager
import com.example.prism.playback.PlayerManager
import com.example.prism.source.SourceStore

class PrismApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Storage.init(this)
        SourceStore.init(this)
        AiStore.init(this)
        PlayerManager.init(this)
        MediaNotificationManager.createChannel(this)
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}