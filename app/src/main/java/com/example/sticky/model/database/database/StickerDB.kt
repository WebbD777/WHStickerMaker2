package com.example.sticky.model.database.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.sticky.model.database.dao.StickerDAO
import com.example.sticky.model.database.dao.StickerPackDAO
import com.example.sticky.model.database.table.StickerPackTable
import com.example.sticky.model.database.table.StickerTable

@Database(
    entities = [StickerPackTable::class, StickerTable::class],
    version = 1,
    exportSchema = true
)
abstract class StickerDB : RoomDatabase() {
    abstract val stickerDao: StickerDAO
    abstract val stickerPackDao: StickerPackDAO
}
