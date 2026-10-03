package com.example.prism.data

data class EqPreset(
    val id: String,
    val name: String,
    val preamp: Float = 0f,
    val bands: List<Float> = List(10) { 0f },
    val isBuiltIn: Boolean = false
)

object BuiltInEqPresets {

    val bandLabels = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")

    val all: List<EqPreset> = listOf(
        EqPreset("flat", "原声", 0f, List(10) { 0f }, true),
        EqPreset("pop", "流行", 0f, listOf(1f, 2f, 3f, 2f, 0f, -1f, -1f, 1f, 2f, 3f), true),
        EqPreset("rock", "摇滚", 0f, listOf(4f, 3f, 1f, -1f, -2f, -1f, 1f, 2f, 3f, 4f), true),
        EqPreset("jazz", "爵士", 0f, listOf(2f, 1f, 0f, 1f, -1f, -1f, 0f, 1f, 2f, 2f), true),
        EqPreset("classical", "古典", 0f, listOf(3f, 2f, 1f, 0f, -1f, -1f, 0f, 1f, 2f, 3f), true),
        EqPreset("bass", "重低音", 2f, listOf(6f, 5f, 4f, 2f, 0f, -1f, -2f, -2f, -1f, 0f), true),
        EqPreset("vocal", "人声", 0f, listOf(-2f, -1f, 0f, 2f, 4f, 4f, 2f, 0f, -1f, -2f), true),
        EqPreset("treble", "清亮", 0f, listOf(-2f, -2f, -1f, 0f, 1f, 2f, 3f, 4f, 5f, 6f), true),
        EqPreset("night", "夜间", 0f, listOf(-3f, -2f, -1f, 0f, 1f, 2f, 2f, 1f, 0f, -1f), true),
        EqPreset("soft", "柔顺", -2f, listOf(-1f, -1f, 0f, 0f, 1f, 1f, 1f, 0f, -1f, -2f), true)
    )
}