package com.example.prism.ai

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AiStore {

    private const val PREFS = "prism_ai"
    private const val KEY_PROVIDERS = "providers"
    private const val KEY_ASSIGNMENT = "assignment"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun loadProviders(): List<AiProvider> {
        val json = prefs.getString(KEY_PROVIDERS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<AiProvider>>() {}.type
            gson.fromJson<List<AiProvider>>(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    fun saveProviders(list: List<AiProvider>) {
        prefs.edit().putString(KEY_PROVIDERS, gson.toJson(list)).apply()
    }

    fun addProvider(p: AiProvider) {
        val list = loadProviders().toMutableList()
        list.removeAll { it.id == p.id }
        list.add(p)
        saveProviders(list)
    }

    fun removeProvider(id: String) {
        saveProviders(loadProviders().filter { it.id != id })
    }

    fun loadAssignment(): AiAssignment {
        val json = prefs.getString(KEY_ASSIGNMENT, null) ?: return AiAssignment()
        return try {
            gson.fromJson(json, AiAssignment::class.java) ?: AiAssignment()
        } catch (_: Exception) { AiAssignment() }
    }

    fun saveAssignment(a: AiAssignment) {
        prefs.edit().putString(KEY_ASSIGNMENT, gson.toJson(a)).apply()
    }

    fun getProvider(id: String?): AiProvider? {
        if (id == null) return null
        return loadProviders().firstOrNull { it.id == id }
    }

    fun getProviderFor(task: String): AiProvider? {
        val assign = loadAssignment()
        val pid = when (task) {
            "lyric_translation" -> assign.lyricTranslation
            "playlist_generation" -> assign.playlistGeneration
            "cover_matching" -> assign.coverMatching
            "lyric_explanation" -> assign.lyricExplanation
            "song_commentary" -> assign.songCommentary
            "mood_analysis" -> assign.moodAnalysis
            "music_story" -> assign.musicStory
            else -> null
        }
        return getProvider(pid) ?: loadProviders().firstOrNull()
    }
}