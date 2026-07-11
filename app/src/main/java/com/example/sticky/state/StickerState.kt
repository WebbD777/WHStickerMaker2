package com.example.sticky.state

import android.net.Uri
data class StickerState(
    val name: String = "",
    val emojis: String = "",
    val isAnimatedSticker: Boolean = false,
    val isSelectingImage: Boolean = false,
    val imageUri: Uri = Uri.EMPTY
)
