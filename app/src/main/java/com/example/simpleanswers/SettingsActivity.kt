package com.example.simpleanswers

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.simpleanswers.data.SettingsPreferencesRepository
import com.example.simpleanswers.databinding.ActivitySettingsBinding
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.domain.model.DeepseekModel

class SettingsActivity : ComponentActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsRepository: SettingsPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsRepository = SettingsPreferencesRepository(applicationContext)

        binding.backButton.setOnClickListener { finish() }

        renderSettings()
        bindModelSelection()
        bindRoleSelection()
    }

    private fun renderSettings() {
        val settings = settingsRepository.getSettings()
        binding.modelGroup.check(
            when (settings.model) {
                DeepseekModel.Flash -> R.id.modelFlashButton
                DeepseekModel.Pro -> R.id.modelProButton
            },
        )
        binding.roleGroup.check(
            when (settings.role) {
                AssistantRole.ChinesePhilosopher -> R.id.rolePhilosopherButton
                AssistantRole.Assistant -> R.id.roleAssistantButton
                AssistantRole.WiseGorilla -> R.id.roleGorillaButton
            },
        )
    }

    private fun bindModelSelection() {
        binding.modelGroup.setOnCheckedChangeListener { _, checkedId ->
            val model = when (checkedId) {
                R.id.modelProButton -> DeepseekModel.Pro
                else -> DeepseekModel.Flash
            }
            settingsRepository.saveModel(model)
        }
    }

    private fun bindRoleSelection() {
        binding.roleGroup.setOnCheckedChangeListener { _, checkedId ->
            val role = when (checkedId) {
                R.id.rolePhilosopherButton -> AssistantRole.ChinesePhilosopher
                R.id.roleGorillaButton -> AssistantRole.WiseGorilla
                else -> AssistantRole.Assistant
            }
            settingsRepository.saveRole(role)
        }
    }
}
