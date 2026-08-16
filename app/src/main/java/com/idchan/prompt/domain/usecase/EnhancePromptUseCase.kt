package com.idchan.prompt.domain.usecase

import com.idchan.prompt.domain.repository.VisionPromptRepository

class EnhancePromptUseCase(
    private val visionRepository: VisionPromptRepository
) {
    suspend operator fun invoke(
        currentPrompt: String,
        action: String,
        imageUri: String? = null
    ): Result<String> {
        return visionRepository.enhancePrompt(currentPrompt, action, imageUri)
    }
}
