package com.idchan.prompt.ui.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.domain.usecase.EnhancePromptUseCase
import com.idchan.prompt.domain.usecase.ManageHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PromptResultUiState(
    val result: AnalysisResult? = null,
    val masterPromptText: String = "",
    val negativePromptText: String = "",
    val isFavorite: Boolean = false,
    val isEnhancing: Boolean = false,
    val userNotification: String? = null
)

class PromptResultViewModel(
    private val enhancePromptUseCase: EnhancePromptUseCase,
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptResultUiState())
    val uiState: StateFlow<PromptResultUiState> = _uiState.asStateFlow()

    fun initResult(analysisResult: AnalysisResult) {
        _uiState.update {
            it.copy(
                result = analysisResult,
                masterPromptText = analysisResult.masterPrompt,
                negativePromptText = analysisResult.negativePrompt,
                isFavorite = analysisResult.isFavorite
            )
        }
    }

    fun onMasterPromptChange(newText: String) {
        _uiState.update { it.copy(masterPromptText = newText) }
    }

    fun onNegativePromptChange(newText: String) {
        _uiState.update { it.copy(negativePromptText = newText) }
    }

    fun toggleFavorite() {
        val current = _uiState.value.result ?: return
        val newFav = !_uiState.value.isFavorite
        _uiState.update { it.copy(isFavorite = newFav) }
        viewModelScope.launch {
            manageHistoryUseCase.toggleFavorite(current.id)
        }
    }

    fun enhancePrompt(action: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEnhancing = true) }
            val current = _uiState.value.masterPromptText
            val enhanced = enhancePromptUseCase(current, action, _uiState.value.result?.imageUri)

            enhanced.fold(
                onSuccess = { newPrompt ->
                    _uiState.update {
                        it.copy(
                            isEnhancing = false,
                            masterPromptText = newPrompt,
                            userNotification = "Prompt updated with $action refinement!"
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isEnhancing = false) }
                }
            )
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }
}
