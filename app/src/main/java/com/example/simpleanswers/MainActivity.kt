package com.example.simpleanswers

import android.content.res.ColorStateList
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.simpleanswers.databinding.ActivityMainBinding
import com.example.simpleanswers.ui.MainViewModel
import com.example.simpleanswers.ui.MainViewModelFactory
import com.example.simpleanswers.ui.model.MainUiState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputHintColors()

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.sendButton.setOnClickListener { sendPrompt() }
        binding.promptEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendPrompt()
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun setupInputHintColors() {
        val defaultHintColor = ContextCompat.getColor(this, R.color.ink)
        val focusedHintColor = ContextCompat.getColor(this, R.color.sage)
        val hintColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(),
            ),
            intArrayOf(focusedHintColor, defaultHintColor),
        )

        binding.promptLayout.defaultHintTextColor = ColorStateList.valueOf(defaultHintColor)
        binding.promptLayout.setHintTextColor(hintColors)
        binding.promptEditText.setHintTextColor(defaultHintColor)
    }

    private fun sendPrompt() {
        viewModel.send(binding.promptEditText.text?.toString().orEmpty())
    }

    private fun render(state: MainUiState) {
        binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.loadingPanel.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.sendButton.isEnabled = !state.isLoading
        binding.promptEditText.isEnabled = !state.isLoading
        binding.promptLayout.error = null

        val answerText = when {
            state.isLoading -> getString(R.string.loading_answer)
            state.answer.isNotBlank() -> state.answer
            else -> getString(R.string.empty_answer)
        }

        binding.answerText.text = answerText
        binding.answerText.setTextColor(
            ContextCompat.getColor(
                this,
                if (state.error == null) R.color.ink else R.color.error_red,
            ),
        )

        if (!state.isLoading) {
            binding.answerScroll.post {
                binding.answerScroll.fullScroll(View.FOCUS_DOWN)
            }
        }
    }
}
