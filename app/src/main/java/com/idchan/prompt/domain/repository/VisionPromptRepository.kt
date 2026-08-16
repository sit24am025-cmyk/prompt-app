package com.idchan.prompt.domain.repository

import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode

interface VisionPromptRepository {
    suspend fun analyzeImage(
        imageUri: String,
        mode: PromptMode,
        detailLevel: PromptDetailLevel,
        apiKey: String? = null
    ): Result<AnalysisResult>

    suspend fun enhancePrompt(
        currentPrompt: String,
        action: String,
        imageUri: String? = null
    ): Result<String>
}
