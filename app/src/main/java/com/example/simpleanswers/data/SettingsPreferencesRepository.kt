package com.example.simpleanswers.data

import android.content.Context
import com.example.simpleanswers.domain.model.AppSettings
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.model.DeepseekModel
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

    private companion object {
        const val PREFERENCES_NAME = "simple_answers_settings"
        const val KEY_MODEL = "deepseek_model"
        const val KEY_ROLE = "assistant_role"
    }
}
