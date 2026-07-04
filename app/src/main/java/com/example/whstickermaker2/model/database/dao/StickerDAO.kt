package com.example.whstickermaker2.model.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.example.whstickermaker2.model.database.table.StickerTable
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDAO {
    @Query("SELECT * FROM sticker ORDER BY createdAt DESC")
    fun getAllStickers(): Flow<List<StickerTable>>

    @Query("DELETE FROM sticker WHERE id = :id")
    suspend fun deleteSticker(id: Int)

    @Upsert
    suspend fun insertStickerPack(stickers: StickerTable)
}