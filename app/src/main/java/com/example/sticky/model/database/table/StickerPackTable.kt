package com.example.sticky.model.database.table

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "sticker_pack")
data class StickerPackTable(
    @PrimaryKey(autoGenerate = true)
    val packId: Int = 0,
    val name: String,
    val trayIcon: String,
    val imageDataVersion: Int,
    val isAnimated: Boolean,
    val createdAt: Long
)
