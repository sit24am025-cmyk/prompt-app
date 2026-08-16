package com.idchan.prompt.domain.repository

import com.idchan.prompt.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryItem>>
    fun getFavorites(): Flow<List<HistoryItem>>
    suspend fun getById(id: String): HistoryItem?
    suspend fun saveHistory(item: HistoryItem)
    suspend fun toggleFavorite(id: String)
    suspend fun deleteHistory(id: String)
    suspend fun clearHistory()
}
