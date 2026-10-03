package com.example.prism.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.prism.MainActivity
import com.example.prism.R

object WidgetUpdateHelper {

    fun updateAll(context: Context) {
        updateSmall(context)
        updateMedium(context)
        updateWide(context)
        updateLarge(context)
    }

    // ---------- PendingIntent ----------
    private fun openApp(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun action(context: Context, act: String, code: Int): PendingIntent {
        val intent = Intent(context, WidgetActionReceiver::class.java).apply {
            action = act
        }
        return PendingIntent.getBroadcast(
            context, code, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ---------- 2×1 ----------
    private fun updateSmall(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(
            ComponentName(context, MusicWidgetSmall::class.java)
        )
        val song = WidgetDataCache.currentSong
        val playing = WidgetDataCache.isPlaying

        ids.forEach { id ->
            val v = RemoteViews(context.packageName, R.layout.widget_music_2x1)
            v.setTextViewText(R.id.widget_title, song?.title ?: "Prism")
            v.setTextViewText(R.id.widget_artist, song?.artist ?: "暂无播放")
            v.setTextViewText(R.id.widget_play, if (playing) "⏸" else "▶")
            v.setOnClickPendingIntent(R.id.widget_root, openApp(context))
            v.setOnClickPendingIntent(
                R.id.widget_play,
                action(context, WidgetActionReceiver.ACTION_TOGGLE, 1)
            )
            mgr.updateAppWidget(id, v)
        }
    }

    // ---------- 2×2 ----------
    private fun updateMedium(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(
            ComponentName(context, MusicWidgetMedium::class.java)
        )
        val song = WidgetDataCache.currentSong
        val playing = WidgetDataCache.isPlaying

        ids.forEach { id ->
            val v = RemoteViews(context.packageName, R.layout.widget_music_2x2)
            v.setTextViewText(R.id.widget_title, song?.title ?: "Prism")
            v.setTextViewText(R.id.widget_artist, song?.artist ?: "暂无播放")
            v.setTextViewText(R.id.widget_play, if (playing) "⏸" else "▶")
            v.setOnClickPendingIntent(R.id.widget_root, openApp(context))
            v.setOnClickPendingIntent(
                R.id.widget_play,
                action(context, WidgetActionReceiver.ACTION_TOGGLE, 1)
            )
            v.setOnClickPendingIntent(
                R.id.widget_prev,
                action(context, WidgetActionReceiver.ACTION_PREV, 2)
            )
            v.setOnClickPendingIntent(
                R.id.widget_next,
                action(context, WidgetActionReceiver.ACTION_NEXT, 3)
            )
            mgr.updateAppWidget(id, v)
        }
    }

    // ---------- 4×1 ----------
    private fun updateWide(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(
            ComponentName(context, MusicWidgetWide::class.java)
        )
        val song = WidgetDataCache.currentSong
        val playing = WidgetDataCache.isPlaying
        val pos = WidgetDataCache.positionMs
        val dur = WidgetDataCache.durationMs

        ids.forEach { id ->
            val v = RemoteViews(context.packageName, R.layout.widget_music_4x1)
            v.setTextViewText(R.id.widget_title, song?.title ?: "Prism")
            v.setTextViewText(R.id.widget_artist, song?.artist ?: "暂无播放")
            v.setTextViewText(R.id.widget_play, if (playing) "⏸" else "▶")
            v.setTextViewText(R.id.widget_pos, fmt(pos))
            v.setTextViewText(R.id.widget_dur, fmt(dur))
            val ratio = if (dur > 0) (pos.toFloat() / dur * 100).toInt() else 0
            v.setProgressBar(R.id.widget_progress, 100, ratio, false)
            v.setOnClickPendingIntent(R.id.widget_root, openApp(context))
            v.setOnClickPendingIntent(
                R.id.widget_play,
                action(context, WidgetActionReceiver.ACTION_TOGGLE, 1)
            )
            v.setOnClickPendingIntent(
                R.id.widget_prev,
                action(context, WidgetActionReceiver.ACTION_PREV, 2)
            )
            v.setOnClickPendingIntent(
                R.id.widget_next,
                action(context, WidgetActionReceiver.ACTION_NEXT, 3)
            )
            mgr.updateAppWidget(id, v)
        }
    }

    // ---------- 4×2 ----------
    private fun updateLarge(context: Context) {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(
            ComponentName(context, MusicWidgetLarge::class.java)
        )
        val song = WidgetDataCache.currentSong
        val playing = WidgetDataCache.isPlaying
        val pos = WidgetDataCache.positionMs
        val dur = WidgetDataCache.durationMs

        ids.forEach { id ->
            val v = RemoteViews(context.packageName, R.layout.widget_music_4x2)
            v.setTextViewText(R.id.widget_title, song?.title ?: "Prism")
            v.setTextViewText(R.id.widget_artist, song?.artist ?: "暂无播放")
            v.setTextViewText(R.id.widget_play, if (playing) "⏸" else "▶")
            v.setTextViewText(R.id.widget_pos, fmt(pos))
            v.setTextViewText(R.id.widget_dur, fmt(dur))
            val ratio = if (dur > 0) (pos.toFloat() / dur * 100).toInt() else 0
            v.setProgressBar(R.id.widget_progress, 100, ratio, false)
            v.setOnClickPendingIntent(R.id.widget_root, openApp(context))
            v.setOnClickPendingIntent(
                R.id.widget_play,
                action(context, WidgetActionReceiver.ACTION_TOGGLE, 1)
            )
            v.setOnClickPendingIntent(
                R.id.widget_prev,
                action(context, WidgetActionReceiver.ACTION_PREV, 2)
            )
            v.setOnClickPendingIntent(
                R.id.widget_next,
                action(context, WidgetActionReceiver.ACTION_NEXT, 3)
            )
            mgr.updateAppWidget(id, v)
        }
    }

    private fun fmt(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }
}