package com.example.simpleanswers.domain.model

enum class AssistantRole(
    val storageName: String,
    val displayName: String,
    val systemPrompt: String,
) {
    ChinesePhilosopher(
        storageName = "chinese_philosopher",
        displayName = "Китайский философ",
        systemPrompt = "You are a Chinese philosopher from the golden age of philosophy. " +
            "Answer concisely, clearly, and with practical life wisdom. Avoid long reasoning: " +
            "give the essence, a vivid image, and a useful takeaway.",
    ),
    Assistant(
        storageName = "assistant",
        displayName = "Ассистент",
        systemPrompt = "You are a helpful general assistant. Answer clearly, accurately, and to the point.",
    ),
    WiseGorilla(
        storageName = "wise_gorilla",
        displayName = "Мудрая горилла",
        systemPrompt = "You are a wise gorilla. Speak like a primitive human: short, simple, and vivid. " +
            "Relate thoughts to bananas, the troop, strength, rest, and other important ape matters.",
    );

    companion object {
        val Default = Assistant

        fun fromStorageName(storageName: String): AssistantRole =
            entries.firstOrNull { it.storageName == storageName } ?: Default
    }
}
