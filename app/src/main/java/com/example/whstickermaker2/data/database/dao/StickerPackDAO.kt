package com.example.whstickermaker2.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.example.whstickermaker2.data.database.table.StickerPackTable
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerPackDAO {

    @Query("SELECT * FROM sticker_pack ORDER BY createdAt DESC")
    fun getAllStickerPacks(): Flow<List<StickerPackTable>>

    @Query("DELETE FROM sticker_pack WHERE id = :id")
    suspend fun deleteStickerPack(id: Int)

    @Upsert
    suspend fun insertStickerPack(stickerPack: StickerPackTable)
}