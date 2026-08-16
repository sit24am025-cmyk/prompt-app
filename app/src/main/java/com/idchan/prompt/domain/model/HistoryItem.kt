package com.idchan.prompt.domain.model

data class HistoryItem(
    val id: String,
    val imageUri: String,
    val masterPrompt: String,
    val negativePrompt: String,
    val mode: PromptMode,
    val timestamp: Long,
    val isFavorite: Boolean
)
