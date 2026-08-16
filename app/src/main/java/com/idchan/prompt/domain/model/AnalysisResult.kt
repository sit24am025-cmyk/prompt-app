package com.idchan.prompt.domain.model

data class AnalysisResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val imageUri: String,
    val masterPrompt: String,
    val negativePrompt: String,
    val mode: PromptMode = PromptMode.EXACT_RECREATION,
    val visualAnalysis: VisualAnalysis = VisualAnalysis(),
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
