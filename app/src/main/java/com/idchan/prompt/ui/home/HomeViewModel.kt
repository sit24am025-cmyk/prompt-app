package com.idchan.prompt.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.domain.usecase.AnalyzeImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedImageUri: Uri? = null,
    val selectedMode: PromptMode = PromptMode.EXACT_RECREATION,
    val isLoading: Boolean = false,
    val analysisResult: AnalysisResult? = null,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val analyzeImageUseCase: AnalyzeImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri, errorMessage = null) }
    }

    fun onModeSelected(mode: PromptMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun analyzeImage(onSuccess: (AnalysisResult) -> Unit) {
        val uri = _uiState.value.selectedImageUri ?: run {
            _uiState.update { it.copy(errorMessage = "Please select an image first.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = analyzeImageUseCase(
                imageUri = uri.toString(),
                overrideMode = _uiState.value.selectedMode
            )

            result.fold(
                onSuccess = { analysis ->
                    _uiState.update { it.copy(isLoading = false, analysisResult = analysis) }
                    onSuccess(analysis)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to analyze image. Please try again."
                        )
                    }
                }
            )
        }
    }

    fun resetImage() {
        _uiState.update { HomeUiState() }
    }
}
