package com.example.sticky.model.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.sticky.model.database.table.StickerTable
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDAO {
    @Query("SELECT * FROM sticker WHERE packId = :packId ORDER BY createdAt DESC")
    fun getStickersByPack(packId: Int): Flow<List<StickerTable>>

    @Query("DELETE FROM sticker WHERE id = :id")
    suspend fun deleteSticker(id: Int)

    @Upsert
    suspend fun insertSticker(sticker: StickerTable)
}
