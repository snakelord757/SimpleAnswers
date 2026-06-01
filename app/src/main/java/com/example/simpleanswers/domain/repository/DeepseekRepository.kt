package com.example.simpleanswers.domain.repository

import com.example.simpleanswers.domain.model.ChatAnswer
import com.example.simpleanswers.domain.model.ChatMessage
import com.example.simpleanswers.domain.model.DeepseekModel

interface DeepseekRepository {
    suspend fun send(
        model: DeepseekModel,
        messages: List<ChatMessage>,
    ): Result<ChatAnswer>
}
