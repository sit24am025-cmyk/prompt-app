package com.idchan.prompt.domain.model

enum class PromptMode(val displayName: String, val description: String) {
    EXACT_RECREATION("Exact Recreation", "Detailed prompt optimized to reproduce the image as closely as possible"),
    PHOTOREALISTIC("Photorealistic", "Optimized for realistic photographic output with detailed optical & texture cues"),
    CINEMATIC("Cinematic", "Emphasizes cinematic lighting, atmosphere, lens characteristics & visual depth"),
    DETAILED("Detailed", "Maximum visual detail describing subject, composition, environment & color"),
    SHORT("Short", "Concise prompt preserving the most essential visual identity");

    companion object {
        fun fromName(name: String): PromptMode {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: EXACT_RECREATION
        }
    }
}
