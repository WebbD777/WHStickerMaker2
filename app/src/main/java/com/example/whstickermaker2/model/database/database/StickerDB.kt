package com.example.whstickermaker2.model.database.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.whstickermaker2.model.database.dao.StickerDAO
import com.example.whstickermaker2.model.database.dao.StickerPackDAO
import com.example.whstickermaker2.model.database.table.StickerPackTable
import com.example.whstickermaker2.model.database.table.StickerTable

@Database(
    entities = [StickerPackTable::class, StickerTable::class],
    version = 1,
    exportSchema = true
)
abstract class StickerDB : RoomDatabase() {
    abstract val stickerDao: StickerDAO
    abstract val stickerPackDao: StickerPackDAO
}