package com.example.sticky.model.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.sticky.model.database.table.StickerPackTable
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerPackDAO {

    @Query("SELECT * FROM sticker_pack ORDER BY createdAt DESC")
    fun getAllStickerPacks(): Flow<List<StickerPackTable>>

    @Query("DELETE FROM sticker_pack WHERE id = :id")
    suspend fun deleteStickerPack(packId: Int)

    @Upsert
    suspend fun insertStickerPack(stickerPack: StickerPackTable)
}
