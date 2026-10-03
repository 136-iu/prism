package com.example.prism.ai

data class AiProvider(
    val id: String,
    val name: String,          // DeepSeek / OpenAI / 自定义
    val baseUrl: String,        // https://api.deepseek.com/v1
    val apiKey: String,
    val model: String,          // deepseek-chat / gpt-4o-mini
    val enabled: Boolean = true
)

data class AiAssignment(
    val lyricTranslation: String? = null,
    val playlistGeneration: String? = null,
    val coverMatching: String? = null,
    val lyricExplanation: String? = null,
    val songCommentary: String? = null,
    val moodAnalysis: String? = null,
    val musicStory: String? = null
)