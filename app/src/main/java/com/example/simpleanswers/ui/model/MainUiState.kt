package com.example.simpleanswers.ui.model

data class MainUiState(
    val answer: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)
