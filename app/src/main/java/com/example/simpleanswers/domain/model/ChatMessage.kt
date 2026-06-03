package com.example.simpleanswers.domain.model

data class ChatMessage(
    val role: Role,
    val content: String,
) {
    enum class Role(val apiName: String) {
        System("system"),
        User("user")
    }
}
