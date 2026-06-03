package com.example.simpleanswers.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpleanswers.BuildConfig
import com.example.simpleanswers.data.DeepseekApiClient
import com.example.simpleanswers.data.DeepseekRepositoryImpl
import com.example.simpleanswers.data.SettingsPreferencesRepository
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.usecase.SendPromptUseCase

class MainViewModelFactory(
    private val context: Context,
    private val roleOverride: AssistantRole? = null,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiClient = DeepseekApiClient(apiKey = BuildConfig.DEEPSEEK_API_KEY)
        val deepseekRepository = DeepseekRepositoryImpl(apiClient)
        val settingsRepository = SettingsPreferencesRepository(context.applicationContext)
        return MainViewModel(
            sendPromptUseCase = SendPromptUseCase(deepseekRepository, settingsRepository),
            roleOverride = roleOverride,
        ) as T
    }
}
