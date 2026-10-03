package com.example.prism.util

object TimeFormatter {

    fun duration(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }

    fun hourMinute(ms: Long): String {
        val s = ms / 1000
        val h = s / 3600
        val m = (s % 3600) / 60
        return if (h > 0) "${h}小时${m}分" else "${m}分"
    }

    fun remaining(ms: Long): String {
        val s = ms / 1000
        return "-%d:%02d".format(s / 60, s % 60)
    }
}