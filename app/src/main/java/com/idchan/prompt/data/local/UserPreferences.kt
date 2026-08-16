package com.idchan.prompt.data.local

import android.content.Context
import android.content.SharedPreferences
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("id_chan_prefs", Context.MODE_PRIVATE)

    var defaultMode: PromptMode
        get() {
            val name = prefs.getString("default_mode", PromptMode.EXACT_RECREATION.name)
            return PromptMode.fromName(name ?: PromptMode.EXACT_RECREATION.name)
        }
        set(value) {
            prefs.edit().putString("default_mode", value.name).apply()
        }

    var detailLevel: PromptDetailLevel
        get() {
            val name = prefs.getString("detail_level", PromptDetailLevel.DETAILED.name)
            return try {
                PromptDetailLevel.valueOf(name ?: PromptDetailLevel.DETAILED.name)
            } catch (e: Exception) {
                PromptDetailLevel.DETAILED
            }
        }
        set(value) {
            prefs.edit().putString("detail_level", value.name).apply()
        }

    var theme: String
        get() = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        set(value) {
            prefs.edit().putString("theme_mode", value).apply()
        }

    var apiKey: String
        get() = prefs.getString("gemini_api_key", "") ?: ""
        set(value) {
            prefs.edit().putString("gemini_api_key", value).apply()
        }

    var groqApiKey: String
        get() = prefs.getString("groq_api_key", "") ?: ""
        set(value) {
            prefs.edit().putString("groq_api_key", value).apply()
        }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) {
            prefs.edit().putBoolean("is_logged_in", value).apply()
        }

    var username: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) {
            prefs.edit().putString("user_name", value).apply()
        }
}
