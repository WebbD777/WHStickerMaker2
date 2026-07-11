package com.example.sticky.event

import android.net.Uri

sealed interface StickerEvent {
    data object SaveSticker : StickerEvent
    data class SetStickerFileName(val name: String) : StickerEvent
    data class SetStickerEmojis(val emojis: String) : StickerEvent
    data class SetStickerIsAnimated(val isAnimated: Boolean) : StickerEvent
    data class ImageSelected(val imageUri: Uri) : StickerEvent
    data class SelectingImage(val isSelectingImage: Boolean) : StickerEvent
}
