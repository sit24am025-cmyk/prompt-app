package com.idchan.prompt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.domain.usecase.ManageHistoryUseCase
import com.idchan.prompt.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val defaultMode: PromptMode = PromptMode.EXACT_RECREATION,
    val detailLevel: PromptDetailLevel = PromptDetailLevel.DETAILED,
    val theme: String = "SYSTEM",
    val apiKey: String = "",
    val groqApiKey: String = "",
    val message: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                defaultMode = settingsRepository.getDefaultMode(),
                detailLevel = settingsRepository.getDetailLevel(),
                theme = settingsRepository.getTheme(),
                apiKey = settingsRepository.getApiKey(),
                groqApiKey = settingsRepository.getGroqApiKey()
            )
        }
    }

    fun setDefaultMode(mode: PromptMode) {
        settingsRepository.setDefaultMode(mode)
        _uiState.update { it.copy(defaultMode = mode) }
    }

    fun setDetailLevel(level: PromptDetailLevel) {
        settingsRepository.setDetailLevel(level)
        _uiState.update { it.copy(detailLevel = level) }
    }

    fun setTheme(theme: String) {
        settingsRepository.setTheme(theme)
        _uiState.update { it.copy(theme = theme) }
    }

    fun setApiKey(key: String) {
        settingsRepository.setApiKey(key)
        _uiState.update { it.copy(apiKey = key, message = "Gemini API Key saved") }
    }

    fun setGroqApiKey(key: String) {
        settingsRepository.setGroqApiKey(key)
        _uiState.update { it.copy(groqApiKey = key, message = "Groq API Key saved") }
    }

    fun clearHistory() {
        viewModelScope.launch {
            manageHistoryUseCase.clearAll()
            _uiState.update { it.copy(message = "History cleared") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
