package com.idchan.prompt.domain.repository

import com.idchan.prompt.domain.model.PromptDetailLevel
import com.idchan.prompt.domain.model.PromptMode

interface SettingsRepository {
    fun getDefaultMode(): PromptMode
    fun setDefaultMode(mode: PromptMode)
    fun getDetailLevel(): PromptDetailLevel
    fun setDetailLevel(level: PromptDetailLevel)
    fun getTheme(): String
    fun setTheme(theme: String)
    fun getApiKey(): String
    fun setApiKey(key: String)
    fun getGroqApiKey(): String
    fun setGroqApiKey(key: String)
}
