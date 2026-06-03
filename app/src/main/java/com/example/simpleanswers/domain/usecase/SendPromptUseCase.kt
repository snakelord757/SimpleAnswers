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
        )
    }

    private fun AppSettings.toSystemPrompt(role: AssistantRole): String {
        val finishRule = finishInstruction.trim()
        return if (finishRule.isEmpty()) {
            role.systemPrompt
        } else {
            role.systemPrompt + "\n\nAdditional completion rule: " + finishRule
        }
    }
}
