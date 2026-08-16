package com.idchan.prompt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.idchan.prompt.domain.model.HistoryItem
import com.idchan.prompt.domain.model.PromptMode

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val imageUri: String,
    val masterPrompt: String,
    val negativePrompt: String,
    val modeName: String,
    val timestamp: Long,
    val isFavorite: Boolean
) {
    fun toDomainModel(): HistoryItem {
        return HistoryItem(
            id = id,
            imageUri = imageUri,
            masterPrompt = masterPrompt,
            negativePrompt = negativePrompt,
            mode = PromptMode.fromName(modeName),
            timestamp = timestamp,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromDomainModel(item: HistoryItem): HistoryEntity {
            return HistoryEntity(
                id = item.id,
                imageUri = item.imageUri,
                masterPrompt = item.masterPrompt,
                negativePrompt = item.negativePrompt,
                modeName = item.mode.name,
                timestamp = item.timestamp,
                isFavorite = item.isFavorite
            )
        }
    }
}
