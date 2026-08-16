package com.idchan.prompt

import android.app.Application
import com.idchan.prompt.data.local.IDChanDatabase
import com.idchan.prompt.data.local.UserPreferences
import com.idchan.prompt.data.remote.GeminiVisionApi
import com.idchan.prompt.data.repository.HistoryRepositoryImpl
import com.idchan.prompt.data.repository.SettingsRepositoryImpl
import com.idchan.prompt.data.repository.VisionPromptRepositoryImpl
import com.idchan.prompt.domain.repository.HistoryRepository
import com.idchan.prompt.domain.repository.SettingsRepository
import com.idchan.prompt.domain.repository.VisionPromptRepository
import com.idchan.prompt.domain.usecase.AnalyzeImageUseCase
import com.idchan.prompt.domain.usecase.EnhancePromptUseCase
import com.idchan.prompt.domain.usecase.ManageHistoryUseCase

class IDChanPromptApp : Application() {

    lateinit var database: IDChanDatabase
        private set

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var geminiVisionApi: GeminiVisionApi
        private set

    lateinit var visionPromptRepository: VisionPromptRepository
        private set

    lateinit var historyRepository: HistoryRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var analyzeImageUseCase: AnalyzeImageUseCase
        private set

    lateinit var enhancePromptUseCase: EnhancePromptUseCase
        private set

    lateinit var manageHistoryUseCase: ManageHistoryUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = IDChanDatabase.getDatabase(this)
        userPreferences = UserPreferences(this)
        geminiVisionApi = GeminiVisionApi(this)

        historyRepository = HistoryRepositoryImpl(database.historyDao())
        settingsRepository = SettingsRepositoryImpl(userPreferences)
        visionPromptRepository = VisionPromptRepositoryImpl(this, geminiVisionApi)

        analyzeImageUseCase = AnalyzeImageUseCase(visionPromptRepository, historyRepository, settingsRepository)
        enhancePromptUseCase = EnhancePromptUseCase(visionPromptRepository)
        manageHistoryUseCase = ManageHistoryUseCase(historyRepository)
    }

    companion object {
        lateinit var instance: IDChanPromptApp
            private set
    }
}
