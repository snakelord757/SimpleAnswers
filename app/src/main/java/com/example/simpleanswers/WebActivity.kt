package com.example.simpleanswers

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.simpleanswers.databinding.ActivityWebBinding
import com.example.simpleanswers.domain.model.AssistantRole
import com.example.simpleanswers.ui.MainViewModel
import com.example.simpleanswers.ui.MainViewModelFactory
import com.example.simpleanswers.ui.model.MainUiState
import kotlinx.coroutines.launch

class WebActivity : ComponentActivity() {
    private lateinit var binding: ActivityWebBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            context = applicationContext,
            roleOverride = AssistantRole.WebAssistant,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInputHintColors()
        setupWebView()

        binding.backButton.setOnClickListener { finish() }
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

    private fun setupWebView() {
        binding.answerWebView.settings.javaScriptEnabled = false
        binding.answerWebView.settings.domStorageEnabled = false
        WebView.setWebContentsDebuggingEnabled(true)
        renderHtml(getString(R.string.empty_web_answer))
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

        when {
            state.isLoading -> renderHtml(getString(R.string.loading_web_answer))
            state.error != null -> renderHtml(state.answer.toErrorHtml())
            state.answer.isNotBlank() -> renderHtml(state.answer)
            else -> renderHtml(getString(R.string.empty_web_answer))
        }
    }

    private fun renderHtml(html: String) {
        binding.answerWebView.loadDataWithBaseURL(
            null,
            html,
            "text/html",
            "UTF-8",
            null,
        )
    }

    private fun String.toErrorHtml(): String = """
        <!doctype html>
        <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 18px;
                        color: #B3261E;
                        font-family: sans-serif;
                        line-height: 1.45;
                        background: #FFFFFF;
                    }
                </style>
            </head>
            <body>${escapeHtml()}</body>
        </html>
    """.trimIndent()

    private fun String.escapeHtml(): String = replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("\n", "<br>")
}
