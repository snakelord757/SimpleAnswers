package com.example.simpleanswers.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepseekApiService {
    @POST("chat/completions")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body body: DeepseekChatRequest,
    ): Response<DeepseekChatResponse>
}
