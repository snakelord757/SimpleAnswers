package com.example.simpleanswers.data

import android.content.Context
import com.example.simpleanswers.domain.model.AppSettings
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.model.DEFAULT_TEMPERATURE
import com.example.simpleanswers.domain.model.DEFAULT_THINKING_ENABLED
import com.example.simpleanswers.domain.model.DeepseekModel
import com.example.simpleanswers.domain.model.MAX_TEMPERATURE
import com.example.simpleanswers.domain.model.MIN_TEMPERATURE
import com.example.simpleanswers.domain.repository.SettingsRepository

class SettingsPreferencesRepository(
    context: Context,
) : SettingsRepository {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getSettings(): AppSettings = AppSettings(
        model = DeepseekModel.fromApiName(
            preferences.getString(KEY_MODEL, DeepseekModel.Default.apiName).orEmpty(),
        ),
        role = AssistantRole.fromStorageName(
            preferences.getString(KEY_ROLE, AssistantRole.Default.storageName).orEmpty(),
        ),
        maxTokens = preferences.getInt(KEY_MAX_TOKENS, NO_TOKEN_LIMIT).takeIf { it > 0 },
        stopSequence = preferences.getString(KEY_STOP_SEQUENCE, "").orEmpty(),
        finishInstruction = preferences.getString(KEY_FINISH_INSTRUCTION, "").orEmpty(),
        temperature = preferences.getFloat(KEY_TEMPERATURE, DEFAULT_TEMPERATURE)
            .coerceIn(MIN_TEMPERATURE, MAX_TEMPERATURE),
        thinkingEnabled = preferences.getBoolean(KEY_THINKING_ENABLED, DEFAULT_THINKING_ENABLED),
    )

    override fun saveModel(model: DeepseekModel) {
        preferences.edit()
            .putString(KEY_MODEL, model.apiName)
            .apply()
    }

    override fun saveRole(role: AssistantRole) {
        preferences.edit()
            .putString(KEY_ROLE, role.storageName)
            .apply()
    }

    override fun saveMaxTokens(maxTokens: Int?) {
        preferences.edit()
            .putInt(KEY_MAX_TOKENS, maxTokens?.takeIf { it > 0 } ?: NO_TOKEN_LIMIT)
            .apply()
    }

    override fun saveStopSequence(stopSequence: String) {
        preferences.edit()
            .putString(KEY_STOP_SEQUENCE, stopSequence)
            .apply()
    }

    override fun saveFinishInstruction(finishInstruction: String) {
        preferences.edit()
            .putString(KEY_FINISH_INSTRUCTION, finishInstruction)
            .apply()
    }

    override fun saveTemperature(temperature: Float) {
        preferences.edit()
            .putFloat(KEY_TEMPERATURE, temperature.coerceIn(MIN_TEMPERATURE, MAX_TEMPERATURE))
            .apply()
    }

    override fun saveThinkingEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_THINKING_ENABLED, enabled)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "simple_answers_settings"
        const val KEY_MODEL = "deepseek_model"
        const val KEY_ROLE = "assistant_role"
        const val KEY_MAX_TOKENS = "max_tokens"
        const val KEY_STOP_SEQUENCE = "stop_sequence"
        const val KEY_FINISH_INSTRUCTION = "finish_instruction"
        const val KEY_TEMPERATURE = "temperature"
        const val KEY_THINKING_ENABLED = "thinking_enabled"
        const val NO_TOKEN_LIMIT = 0
    }
}
