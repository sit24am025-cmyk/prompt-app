package com.idchan.prompt.data.repository

import com.idchan.prompt.data.local.dao.HistoryDao
import com.idchan.prompt.data.local.entity.HistoryEntity
import com.idchan.prompt.domain.model.HistoryItem
import com.idchan.prompt.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<HistoryItem>> {
        return historyDao.getAllHistory().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getFavorites(): Flow<List<HistoryItem>> {
        return historyDao.getFavorites().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getById(id: String): HistoryItem? {
        return historyDao.getById(id)?.toDomainModel()
    }

    override suspend fun saveHistory(item: HistoryItem) {
        historyDao.insert(HistoryEntity.fromDomainModel(item))
    }

    override suspend fun toggleFavorite(id: String) {
        val existing = historyDao.getById(id) ?: return
        val updated = existing.copy(isFavorite = !existing.isFavorite)
        historyDao.update(updated)
    }

    override suspend fun deleteHistory(id: String) {
        historyDao.deleteById(id)
    }

    override suspend fun clearHistory() {
        historyDao.clearAll()
    }
}
