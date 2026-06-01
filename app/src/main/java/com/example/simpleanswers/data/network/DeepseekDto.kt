package com.example.simpleanswers.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeepseekChatRequest(
    val model: String,
    val messages: List<DeepseekMessageDto>,
    val stream: Boolean = false,
)

@Serializable
data class DeepseekMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class DeepseekChatResponse(
    val choices: List<DeepseekChoiceDto> = emptyList(),
)

@Serializable
data class DeepseekChoiceDto(
    val message: DeepseekMessageDto,
    @SerialName("finish_reason")
    val finishReason: String? = null,
)
