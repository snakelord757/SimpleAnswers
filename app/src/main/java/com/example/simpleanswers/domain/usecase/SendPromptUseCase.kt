package com.example.simpleanswers.domain.usecase

import com.example.simpleanswers.domain.model.AppSettings
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.model.ChatAnswer
import com.example.simpleanswers.domain.model.ChatMessage
import com.example.simpleanswers.domain.repository.DeepseekRepository
import com.example.simpleanswers.domain.repository.SettingsRepository

class SendPromptUseCase(
    private val deepseekRepository: DeepseekRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(
        prompt: String,
        roleOverride: AssistantRole? = null,
    ): Result<ChatAnswer> {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите текст запроса"))
        }

        val settings = settingsRepository.getSettings()
        val role = roleOverride ?: settings.role

        return deepseekRepository.send(
            model = settings.model,
            messages = listOf(
                ChatMessage(
                    role = ChatMessage.Role.System,
                    content = settings.toSystemPrompt(role),
                ),
                ChatMessage(role = ChatMessage.Role.User, content = trimmedPrompt),
            ),
            maxTokens = settings.maxTokens,
            stopSequence = settings.stopSequence.takeIf(String::isNotBlank),
            temperature = settings.temperature,
            thinkingEnabled = settings.thinkingEnabled,
        )
    }

    private fun AppSettings.toSystemPrompt(role: AssistantRole): String {
        val finishRule = finishInstruction.trim()
        return buildString {
            append(BASE_SYSTEM_PROMPT)
            append("\n\n")
            append(role.systemPrompt)
            if (finishRule.isNotEmpty()) {
                append("\n\nAdditional completion rule: ")
                append(finishRule)
            }
        }
    }

    private companion object {
        const val BASE_SYSTEM_PROMPT = "Always answer in Russian."
    }
}
