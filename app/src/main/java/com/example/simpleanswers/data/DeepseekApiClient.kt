package com.example.simpleanswers.data

import android.util.Log
import com.example.simpleanswers.data.network.DeepseekApiService
import com.example.simpleanswers.data.network.DeepseekChatRequest
import com.example.simpleanswers.data.network.DeepseekMessageDto
import com.example.simpleanswers.data.network.DeepseekResponseLoggingInterceptor
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
        maxTokens: Int?,
        stopSequence: String?,
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
            maxTokens = maxTokens?.takeIf { it > 0 },
            stop = stopSequence?.takeIf(String::isNotBlank),
        )

        val response = apiService.sendMessage(
            authorization = "Bearer $apiKey",
            body = request,
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string().orEmpty()
            Log.e(TAG, "Deepseek error ${response.code()}: ${errorBody.ifBlank { "empty response" }}")
            throw IllegalStateException("Deepseek вернул ${response.code()}: ${errorBody.ifBlank { "пустой ответ" }}")
        }

        val responseBody = response.body()
        val choice = responseBody?.choices?.firstOrNull()
        val content = choice
            ?.message
            ?.content
            ?.takeIf(String::isNotBlank)
            ?: throw IllegalStateException("Deepseek вернул пустой ответ")

        Log.i(
            TAG,
            "Deepseek parsed answer: model=${model.apiName}, finishReason=${choice.finishReason}, content=$content",
        )

        return ChatAnswer(content = content)
    }

    private companion object {
        const val TAG = "DeepseekApiClient"
        const val DEEPSEEK_BASE_URL = "https://api.deepseek.com/"
        const val TIMEOUT_SECONDS = 30L

        fun createApiService(): DeepseekApiService {
            val json = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                redactHeader("Authorization")
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(DeepseekResponseLoggingInterceptor())
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
