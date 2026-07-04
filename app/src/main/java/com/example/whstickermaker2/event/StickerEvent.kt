package com.example.whstickermaker2.event

sealed interface StickerEvent {
    object SaveSticker : StickerEvent
    data class SetStickerFilaName(val name: String) : StickerPackEvent
    data class SetStickerEmojis(val emojis: String) : StickerPackEvent
    data class SetStickerIsAnimated(val isAnimated: Boolean) : StickerPackEvent
    data class SetStickerOrder(val order: Int) : StickerPackEvent
}