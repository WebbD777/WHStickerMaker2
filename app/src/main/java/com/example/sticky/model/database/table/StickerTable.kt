package com.example.sticky.model.database.table

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "sticker",
    foreignKeys = [
        ForeignKey(
            entity = StickerPackTable::class,
            parentColumns = ["packId"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packId")]
)
data class StickerTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packId: Int,
    val fileName: String,
    val emojis: String,
    val isAnimated: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
