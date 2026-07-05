package com.example.whstickermaker2.state

data class StickerState(
    val name: String = "",
    val emojis: String = "",
    val isAnimatedSticker: Boolean = false,
    val isAddingSticker: Boolean = false
)
