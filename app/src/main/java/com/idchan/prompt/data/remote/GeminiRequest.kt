package com.idchan.prompt.data.remote

import com.google.gson.annotations.SerializedName

data class GeminiRequestBody(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig? = GeminiGenerationConfig()
)

data class GeminiContent(
    @SerializedName("role") val role: String = "user",
    @SerializedName("parts") val parts: List<GeminiPart>
)

sealed class GeminiPart {
    data class TextPart(
        @SerializedName("text") val text: String
    ) : GeminiPart()

    data class InlineDataPart(
        @SerializedName("inline_data") val inlineData: InlineData
    ) : GeminiPart()
}

data class InlineData(
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("data") val data: String
)

data class GeminiGenerationConfig(
    @SerializedName("temperature") val temperature: Float = 0.2f,
    @SerializedName("topK") val topK: Int = 40,
    @SerializedName("topP") val topP: Float = 0.95f,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 2048
)
