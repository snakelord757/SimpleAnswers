package com.example.simpleanswers.domain.usecase

import com.example.simpleanswers.domain.model.ChatAnswer
import com.example.simpleanswers.domain.model.ChatMessage
import com.example.simpleanswers.domain.repository.DeepseekRepository
import com.example.simpleanswers.domain.repository.SettingsRepository

class SendPromptUseCase(
    private val deepseekRepository: DeepseekRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(prompt: String): Result<ChatAnswer> {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите текст запроса"))
        }

        val settings = settingsRepository.getSettings()

        return deepseekRepository.send(
            model = settings.model,
            messages = listOf(
                ChatMessage(
                    role = ChatMessage.Role.System,
                    content = settings.role.systemPrompt,
                ),
                ChatMessage(role = ChatMessage.Role.User, content = trimmedPrompt),
            ),
        )
    }
}
