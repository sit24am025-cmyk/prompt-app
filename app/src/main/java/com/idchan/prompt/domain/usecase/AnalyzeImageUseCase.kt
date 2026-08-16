package com.idchan.prompt.domain.usecase

import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.domain.model.HistoryItem
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.domain.repository.HistoryRepository
import com.idchan.prompt.domain.repository.SettingsRepository
import com.idchan.prompt.domain.repository.VisionPromptRepository

class AnalyzeImageUseCase(
    private val visionRepository: VisionPromptRepository,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        imageUri: String,
        overrideMode: PromptMode? = null,
        overrideDetailLevel: PromptDetailLevel? = null
    ): Result<AnalysisResult> {
        val mode = overrideMode ?: settingsRepository.getDefaultMode()
        val detailLevel = overrideDetailLevel ?: settingsRepository.getDetailLevel()
        val apiKey = settingsRepository.getApiKey().ifBlank { null }

        val result = visionRepository.analyzeImage(
            imageUri = imageUri,
            mode = mode,
            detailLevel = detailLevel,
            apiKey = apiKey
        )

        result.getOrNull()?.let { analysis ->
            // Save to room DB history automatically
            val historyItem = HistoryItem(
                id = analysis.id,
                imageUri = analysis.imageUri,
                masterPrompt = analysis.masterPrompt,
                negativePrompt = analysis.negativePrompt,
                mode = analysis.mode,
                timestamp = analysis.timestamp,
                isFavorite = false
            )
            historyRepository.saveHistory(historyItem)
        }

        return result
    }
}
