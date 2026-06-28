package com.example.whstickermaker2.data.database.table

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "sticker_pack")
data class StickerPackTable(
    @PrimaryKey(autoGenerate = true)
    val packId: Int? = 0,
    val name: String,
    val trayIconFileName: String,
    val imageDataVersion: Int,
    val isAnimated: Boolean,
    val createdAt: Long
)