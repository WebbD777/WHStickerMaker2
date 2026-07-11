package com.example.sticky.data

import android.content.Context
import androidx.room3.Room
import com.example.sticky.model.database.database.StickerDB

object DatabaseProvider {
    @Volatile
    private var INSTANCE: StickerDB? = null // Renamed to StickerDB

    fun getDatabase(context: Context): StickerDB {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                StickerDB::class.java,
                "stickers.db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}