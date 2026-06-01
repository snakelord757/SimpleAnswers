package com.example.simpleanswers.domain.model

enum class DeepseekModel(
    val apiName: String,
    val displayName: String,
) {
    Flash("deepseek-v4-flash", "deepseek-v4-flash"),
    Pro("deepseek-v4-pro", "deepseek-v4-pro");

    companion object {
        val Default = Flash

        fun fromApiName(apiName: String): DeepseekModel =
            entries.firstOrNull { it.apiName == apiName } ?: Default
    }
}
