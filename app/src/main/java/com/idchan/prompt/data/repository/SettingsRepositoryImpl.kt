package com.idchan.prompt.data.repository

import com.idchan.prompt.data.local.UserPreferences
import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode
import com.idchan.prompt.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val prefs: UserPreferences
) : SettingsRepository {

    override fun getDefaultMode(): PromptMode = prefs.defaultMode

    override fun setDefaultMode(mode: PromptMode) {
        prefs.defaultMode = mode
    }

    override fun getDetailLevel(): PromptDetailLevel = prefs.detailLevel

    override fun setDetailLevel(level: PromptDetailLevel) {
        prefs.detailLevel = level
    }

    override fun getTheme(): String = prefs.theme

    override fun setTheme(theme: String) {
        prefs.theme = theme
    }

    override fun getApiKey(): String = prefs.apiKey

    override fun setApiKey(key: String) {
        prefs.apiKey = key
    }

    override fun getGroqApiKey(): String = prefs.groqApiKey

    override fun setGroqApiKey(key: String) {
        prefs.groqApiKey = key
    }
}
