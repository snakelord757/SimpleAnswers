package com.example.simpleanswers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
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
        binding.openWebModeButton.setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }

        setupKeyboardAwareScroll()
        renderSettings()
        bindModelSelection()
        bindRoleSelection()
        bindAnswerLimitFields()
    }

    private fun setupKeyboardAwareScroll() {
        val initialTopPadding = binding.settingsScroll.paddingTop
        val initialBottomPadding = binding.settingsScroll.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsScroll) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(
                view.paddingLeft,
                initialTopPadding + systemBars.top,
                view.paddingRight,
                initialBottomPadding + maxOf(imeBottom, systemBars.bottom),
            )
            insets
        }

        listOf(
            binding.maxTokensEditText,
            binding.stopSequenceEditText,
            binding.finishInstructionEditText,
        ).forEach { editText ->
            editText.setOnFocusChangeListener { focusedView, hasFocus ->
                if (hasFocus) {
                    binding.settingsScroll.postDelayed(
                        { scrollToFocusedField(focusedView) },
                        SCROLL_DELAY_MS,
                    )
                }
            }
        }
    }

    private fun scrollToFocusedField(focusedView: View) {
        val scrollLocation = IntArray(2)
        val fieldLocation = IntArray(2)
        binding.settingsScroll.getLocationOnScreen(scrollLocation)
        focusedView.getLocationOnScreen(fieldLocation)

        val fieldBottomInsideScroll = fieldLocation[1] - scrollLocation[1] + focusedView.height
        val visibleBottom = binding.settingsScroll.height - binding.settingsScroll.paddingBottom
        val overlap = fieldBottomInsideScroll - visibleBottom

        if (overlap > 0) {
            binding.settingsScroll.smoothScrollBy(0, overlap + EXTRA_FIELD_MARGIN_PX)
        }
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
                AssistantRole.WebAssistant -> R.id.roleWebAssistantButton
                AssistantRole.WiseGorilla -> R.id.roleGorillaButton
            },
        )
        binding.maxTokensEditText.setText((settings.maxTokens ?: 0).toString())
        binding.stopSequenceEditText.setText(settings.stopSequence)
        binding.finishInstructionEditText.setText(settings.finishInstruction)
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
                R.id.roleWebAssistantButton -> AssistantRole.WebAssistant
                R.id.roleGorillaButton -> AssistantRole.WiseGorilla
                else -> AssistantRole.Assistant
            }
            settingsRepository.saveRole(role)
        }
    }

    private fun bindAnswerLimitFields() {
        binding.maxTokensEditText.doAfterTextChanged { editable ->
            val maxTokens = editable?.toString()
                ?.trim()
                ?.toIntOrNull()
                ?.takeIf { it > 0 }
            settingsRepository.saveMaxTokens(maxTokens)
        }
        binding.stopSequenceEditText.doAfterTextChanged { editable ->
            settingsRepository.saveStopSequence(editable?.toString().orEmpty())
        }
        binding.finishInstructionEditText.doAfterTextChanged { editable ->
            settingsRepository.saveFinishInstruction(editable?.toString().orEmpty())
        }
    }

    private companion object {
        const val SCROLL_DELAY_MS = 250L
        const val EXTRA_FIELD_MARGIN_PX = 32
    }
}
