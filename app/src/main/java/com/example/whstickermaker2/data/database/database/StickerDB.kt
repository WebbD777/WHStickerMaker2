package com.example.whstickermaker2.data.database.database

import androidx.room3.Database

@Database(
    entities = [StickerPackTable::class],
    version = 1,
    exportSchema = true
)
abstract class StickerDB {
}