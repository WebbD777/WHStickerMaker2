package com.example.whstickermaker2.event

sealed interface StickerPackEvent {
    object SaveStickerPack : StickerPackEvent
    data class SetStickerPackName(val name: String) : StickerPackEvent
    data class SetStickerPackTrayIcon(val trayIcon: String) : StickerPackEvent
    data class SetStickerPackImageDataVersion(val imageDataVersion: Int) : StickerPackEvent
    data class SetStickerPackIsAnimated(val isAnimated: Boolean) : StickerPackEvent
    data class SetStickerPackCreatedAt(val createdAt: Long) : StickerPackEvent
    data class ShowDialog(val isAddingPack: Boolean) : StickerPackEvent
    data class HideDialog(val isAddingPack: Boolean) : StickerPackEvent
}