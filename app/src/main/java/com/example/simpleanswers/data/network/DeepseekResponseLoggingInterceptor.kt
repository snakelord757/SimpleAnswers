package com.example.simpleanswers.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class DeepseekResponseLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBody = response.peekBody(MAX_LOG_BYTES)
        val bodyText = responseBody.string().ifBlank { "empty response" }
        val message = "HTTP ${response.code} ${response.request.url}: $bodyText"

        if (response.isSuccessful) {
            Log.i(TAG, message)
        } else {
            Log.e(TAG, message)
        }

        return response
    }

    private companion object {
        const val TAG = "DeepseekHttpResponse"
        const val MAX_LOG_BYTES = 256_000L
    }
}
