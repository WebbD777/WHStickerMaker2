package com.example.whstickermaker2.state

data class StickerState(
    val name: String = "",
    val emojis: String = "",
    val isAnimated: Boolean = false,
    val order: Int = 0,
    val isAddingSticker: Boolean = false
)
