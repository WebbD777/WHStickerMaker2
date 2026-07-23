package com.example.sticky.model.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.example.sticky.model.database.table.StickerPackTable
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerPackDAO {

    //for "reactive" programming—it keeps an open connection to the database and emits new values whenever the data changes. This is perfect for your UI
    @Query("SELECT * FROM sticker_pack ORDER BY createdAt DESC")
    fun getAllStickerPacks(): Flow<List<StickerPackTable>>

    @Query("SELECT * FROM sticker_pack ORDER BY createdAt DESC")
    fun getAllStickerPacksSync(): List<StickerPackTable>

    @Query("SELECT * FROM sticker_pack WHERE packId = :packId")
    fun getStickerPackSync(packId: Int): StickerPackTable?

    @Query("SELECT * FROM sticker_pack WHERE packId = :packId")
    suspend fun getStickerPack(packId: Int): StickerPackTable?

    @Query("DELETE FROM sticker_pack WHERE packId = :packId")
    suspend fun deleteStickerPack(packId: Int)

    @Upsert
    suspend fun insertStickerPack(stickerPack: StickerPackTable)

    @Query("UPDATE sticker_pack SET trayIcon = :trayIcon, imageDataVersion = imageDataVersion + 1 WHERE packId = :packId")
    suspend fun updateTrayIcon(packId: Int, trayIcon: String)

    @Query("SELECT fileName FROM sticker WHERE packId = :packId ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirstStickerPath(packId: Int): String?
}
