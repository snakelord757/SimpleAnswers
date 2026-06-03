package com.example.simpleanswers.domain.repository

import com.example.simpleanswers.domain.model.AppSettings
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.model.DeepseekModel

interface SettingsRepository {
    fun getSettings(): AppSettings
    fun saveModel(model: DeepseekModel)
    fun saveRole(role: AssistantRole)
    fun saveMaxTokens(maxTokens: Int?)
    fun saveStopSequence(stopSequence: String)
    fun saveFinishInstruction(finishInstruction: String)
}
