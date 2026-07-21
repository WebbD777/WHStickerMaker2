package com.example.sticky.state

data class StickerPackState(
    val name: String = "",
    val trayIcon: String = "",
    val imageDataVersion: Int = 0,
    val isAnimated: Boolean = false,
    val createdAt: Long = 0,
    val isAddingStickerPack: Boolean = false,
    val isSeeingStickers: Boolean = false,
    val isSelectingTrayIcon: Boolean = false
)
