package com.example.simpleanswers.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleanswers.domain.usecase.SendPromptUseCase
import com.example.simpleanswers.ui.model.MainUiState
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sendPromptUseCase: SendPromptUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun send(prompt: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(answer = "", isLoading = true, error = null) }

            sendPromptUseCase(prompt)
                .onSuccess { answer ->
                    _uiState.update {
                        it.copy(answer = answer.content, isLoading = false, error = null)
                    }
                }
                .onFailure { throwable ->
                    val message = throwable.toUserMessage()
                    _uiState.update {
                        it.copy(
                            answer = "Не удалось получить ответ.\n\n$message",
                            isLoading = false,
                            error = message,
                        )
                    }
                }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is UnknownHostException -> "Нет соединения с интернетом или DNS не смог найти сервер Deepseek."
        is SocketTimeoutException -> "Сервер Deepseek не ответил за отведенное время. Попробуйте еще раз."
        is IOException -> "Сетевая ошибка: ${message ?: "проверьте подключение и повторите запрос"}."
        is IllegalArgumentException -> message ?: "Проверьте введенные данные."
        is IllegalStateException -> message ?: "Deepseek вернул некорректный ответ."
        else -> message ?: "Произошла неизвестная ошибка."
    }
}
