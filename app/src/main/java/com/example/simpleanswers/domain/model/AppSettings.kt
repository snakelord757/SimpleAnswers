package com.example.simpleanswers.domain.model

data class AppSettings(
    val model: DeepseekModel = DeepseekModel.Default,
    val role: AssistantRole = AssistantRole.Default,
    val maxTokens: Int? = null,
    val stopSequence: String = "",
    val finishInstruction: String = "",
)
