package com.example.simpleanswers.data

import com.example.simpleanswers.domain.model.ChatAnswer
import com.example.simpleanswers.domain.model.ChatMessage
import com.example.simpleanswers.domain.model.DeepseekModel
import com.example.simpleanswers.domain.repository.DeepseekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepseekRepositoryImpl(
    private val apiClient: DeepseekApiClient,
) : DeepseekRepository {
    override suspend fun send(
        model: DeepseekModel,
        messages: List<ChatMessage>,
        maxTokens: Int?,
        stopSequence: String?,
    ): Result<ChatAnswer> = withContext(Dispatchers.IO) {
        runCatching {
            apiClient.send(
                model = model,
                messages = messages,
                maxTokens = maxTokens,
                stopSequence = stopSequence,
            )
        }
    }
}
