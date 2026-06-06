package com.example.simpleanswers.domain.model

data class AppSettings(
    val model: DeepseekModel = DeepseekModel.Default,
    val role: AssistantRole = AssistantRole.Default,
    val maxTokens: Int? = null,
    val stopSequence: String = "",
    val finishInstruction: String = "",
    val temperature: Float = DEFAULT_TEMPERATURE,
    val thinkingEnabled: Boolean = DEFAULT_THINKING_ENABLED,
)

const val DEFAULT_THINKING_ENABLED = true
const val DEFAULT_TEMPERATURE = 0.0f
const val MIN_TEMPERATURE = 0.0f
const val MAX_TEMPERATURE = 2.0f
const val TEMPERATURE_STEP = 0.1f
