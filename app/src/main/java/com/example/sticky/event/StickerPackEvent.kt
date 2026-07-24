package com.example.sticky.event

import android.content.Context
import com.example.sticky.model.database.table.StickerPackTable

sealed interface StickerPackEvent {
    data object SaveStickerPack : StickerPackEvent
    data class SetStickerPackName(val name: String) : StickerPackEvent
    data class SetStickerPackTrayIcon(val trayIcon: String) : StickerPackEvent
    data class SetStickerPackImageDataVersion(val imageDataVersion: Int) : StickerPackEvent
    data class SetStickerPackIsAnimated(val isAnimated: Boolean) : StickerPackEvent
    data class SetStickerPackCreatedAt(val createdAt: Long) : StickerPackEvent
    data class ShowDialog(val isAddingPack: Boolean) : StickerPackEvent
    data class HideDialog(val isAddingPack: Boolean) : StickerPackEvent
    data class SeedingStickers(val isSeeingStickers: Boolean) : StickerPackEvent
    data class SelectingTrayIcon(val isSelectingTrayIcon: Boolean) : StickerPackEvent
    data class DoneSeedingStickers(val isSeeingStickers: Boolean) : StickerPackEvent
    data class DoneSelectingTrayIcon(val isSelectingTrayIcon: Boolean) : StickerPackEvent
    data class ExportToWhatsApp(val pack: StickerPackTable, val context: Context, val onResult: (Boolean, String?) -> Unit) : StickerPackEvent
}
