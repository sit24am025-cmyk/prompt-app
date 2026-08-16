package com.idchan.prompt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.idchan.prompt.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_table WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Update
    suspend fun update(entity: HistoryEntity)

    @Query("DELETE FROM history_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM history_table")
    suspend fun clearAll()
}
