package com.idchan.prompt.data.remote

import com.google.gson.annotations.SerializedName

data class GeminiResponseBody(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiResponseContent?
)

data class GeminiResponseContent(
    @SerializedName("parts") val parts: List<GeminiResponsePart>?
)

data class GeminiResponsePart(
    @SerializedName("text") val text: String?
)
