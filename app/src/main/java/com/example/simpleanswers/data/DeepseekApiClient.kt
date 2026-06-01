package com.example.simpleanswers.data

import com.example.simpleanswers.data.network.DeepseekApiService
import com.example.simpleanswers.data.network.DeepseekChatRequest
import com.example.simpleanswers.data.network.DeepseekMessageDto
import com.example.simpleanswers.domain.model.ChatAnswer
import com.example.simpleanswers.domain.model.ChatMessage
import com.example.simpleanswers.domain.model.DeepseekModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class DeepseekApiClient(
    private val apiKey: String,
    private val apiService: DeepseekApiService = createApiService(),
) {
    suspend fun send(
        model: DeepseekModel,
        messages: List<ChatMessage>,
    ): ChatAnswer {
        require(apiKey.isNotBlank()) { "Добавьте DEEPSEEK_API_KEY в local.properties" }

        val request = DeepseekChatRequest(
            model = model.apiName,
            messages = messages.map { message ->
                DeepseekMessageDto(
                    role = message.role.apiName,
                    content = message.content,
                )
            },
        )

        val response = apiService.sendMessage(
            authorization = "Bearer $apiKey",
            body = request,
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string().orEmpty()
            throw IllegalStateException("Deepseek вернул ${response.code()}: ${errorBody.ifBlank { "пустой ответ" }}")
        }

        val content = response.body()
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.takeIf(String::isNotBlank)
            ?: throw IllegalStateException("Deepseek вернул пустой ответ")

        return ChatAnswer(content = content)
    }

    private companion object {
        const val DEEPSEEK_BASE_URL = "https://api.deepseek.com/"
        const val TIMEOUT_SECONDS = 30L

        fun createApiService(): DeepseekApiService {
            val json = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(DEEPSEEK_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(DeepseekApiService::class.java)
        }
    }
}
