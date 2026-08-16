package com.idchan.prompt.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.gson.Gson
import com.idchan.prompt.domain.model.AnalysisResult
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class GeminiVisionApi(private val context: Context) {

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val SYSTEM_PROMPT = """
You are an expert visual reverse-engineering and image-prompt reconstruction system. 
Analyze the supplied reference image at maximum useful visual detail. 
Your purpose is not merely to describe the image. 
Your purpose is to determine the visual instructions an image-generation model would need to reproduce the reference image as closely as possible. 
Carefully analyze the subject, appearance, pose, spatial relationships, composition, framing, camera perspective, lens characteristics, lighting, shadows, environment, background, colors, materials, textures, depth of field, style, atmosphere and image quality. 
Preserve the relative position and scale of important elements. 
Distinguish visible facts from uncertain inferences and never invent details that cannot reasonably be inferred. 
Produce a coherent, highly detailed image-generation prompt optimized for visual similarity.

Respond with a JSON object in this exact structure:
{
  "masterPrompt": "Complete high-fidelity prompt...",
  "negativePrompt": "distorted anatomy, extra limbs, blurry, low resolution, bad lighting...",
  "subjectDetails": "...",
  "environmentDetails": "...",
  "compositionDetails": "...",
  "cameraDetails": "...",
  "lightingDetails": "...",
  "colorDetails": "...",
  "styleDetails": "...",
  "textureDetails": "..."
}
"""
    }

    suspend fun analyzeImageWithGemini(
        imageUri: String,
        mode: PromptMode,
        detailLevel: PromptDetailLevel,
        apiKey: String
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            val base64Image = readImageAsBase64(Uri.parse(imageUri))
                ?: return@withContext Result.failure(IllegalArgumentException("Failed to load image file"))

            val userInstruction = "Analyze this image in mode: ${mode.displayName} with detail level: ${detailLevel.displayName}. $SYSTEM_PROMPT"

            val requestParts = listOf(
                mapOf("text" to userInstruction),
                mapOf(
                    "inline_data" to mapOf(
                        "mime_type" to "image/jpeg",
                        "data" to base64Image
                    )
                )
            )

            val payload = mapOf(
                "contents" to listOf(
                    mapOf(
                        "role" to "user",
                        "parts" to requestParts
                    )
                )
            )

            val jsonBody = gson.toJson(payload)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isEmpty()) {
                return@withContext Result.failure(Exception("Gemini API Error: ${response.code} ${response.message}"))
            }

            val geminiResp = gson.fromJson(responseString, GeminiResponseBody::class.java)
            val rawText = geminiResp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            val cleanedJson = cleanJsonResponse(rawText)
            val jsonMap = try {
                gson.fromJson(cleanedJson, Map::class.java) as Map<String, Any>
            } catch (e: Exception) {
                null
            }

            val masterPrompt = jsonMap?.get("masterPrompt") as? String ?: rawText
            val negativePrompt = jsonMap?.get("negativePrompt") as? String
                ?: "distorted anatomy, extra fingers, blurry details, incorrect proportions, low resolution, unnatural skin, bad lighting"

            val result = AnalysisResult(
                imageUri = imageUri,
                masterPrompt = masterPrompt,
                negativePrompt = negativePrompt,
                mode = mode
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun readImageAsBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return null

            // Scale down if image is huge to avoid OOM
            val scaledBitmap = scaleBitmapIfNeeded(bitmap, 1280)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun cleanJsonResponse(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```json")) {
            text = text.removePrefix("```json")
        } else if (text.startsWith("```")) {
            text = text.removePrefix("```")
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```")
        }
        return text.trim()
    }
}
