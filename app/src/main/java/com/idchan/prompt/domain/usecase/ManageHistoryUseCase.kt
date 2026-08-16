package com.idchan.prompt.domain.usecase

import com.idchan.prompt.domain.model.HistoryItem
import com.idchan.prompt.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class ManageHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    fun getAllHistory(): Flow<List<HistoryItem>> = historyRepository.getAllHistory()
    fun getFavorites(): Flow<List<HistoryItem>> = historyRepository.getFavorites()
    suspend fun toggleFavorite(id: String) = historyRepository.toggleFavorite(id)
    suspend fun deleteHistory(id: String) = historyRepository.deleteHistory(id)
    suspend fun clearAll() = historyRepository.clearHistory()
}
