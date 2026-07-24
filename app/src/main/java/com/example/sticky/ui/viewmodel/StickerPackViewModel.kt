package com.example.sticky.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sticky.event.StickerPackEvent
import com.example.sticky.model.database.dao.StickerDAO
import com.example.sticky.model.database.dao.StickerPackDAO
import com.example.sticky.model.database.table.StickerPackTable
import com.example.sticky.state.StickerPackState
import com.example.sticky.utils.image.convertImageToTrayIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import android.net.Uri

class StickerPackViewModel(
    private val dao : StickerPackDAO,
    private val stickerDao: StickerDAO
) : ViewModel(){

    // handles user input like name, author, etc
    private val _state = MutableStateFlow(StickerPackState())
    // handles the list from the database
    val stickerPacks = dao.getAllStickerPacks()

    val state = _state.asStateFlow()

    suspend fun getFirstStickerPath(packId: Int): String? {
        return dao.getFirstStickerPath(packId)
    }

    fun onEvent(event: StickerPackEvent){
        when(event){
            StickerPackEvent.SaveStickerPack -> {
                val name = state.value.name
                val trayIcon = state.value.trayIcon
                val imageDataVersion = state.value.imageDataVersion
                val isAnimated = state.value.isAnimated
                val createdAt = state.value.createdAt

                val stickerPack = StickerPackTable(
                    name = name,
                    trayIcon = trayIcon,
                    imageDataVersion = imageDataVersion,
                    isAnimated = isAnimated,
                    createdAt = createdAt
                )

                // Insert the sticker pack into the database
                // using a coroutine
                viewModelScope.launch {
                    dao.insertStickerPack(stickerPack)
                    
                }

                // Done adding pack
                _state.update { it.copy(
                    isAddingStickerPack = false,
                    name = "",
                    trayIcon = "",
                ) }
            }
            is StickerPackEvent.SetStickerPackCreatedAt -> {
                _state.update { it.copy(
                    createdAt = event.createdAt
                ) }
            }
            is StickerPackEvent.SetStickerPackImageDataVersion -> {
                _state.update { it.copy(
                    imageDataVersion = event.imageDataVersion
                ) }
            }
            is StickerPackEvent.SetStickerPackIsAnimated -> {
                _state.update {
                    it.copy(
                        isAnimated = event.isAnimated
                    )
                }
            }
            is StickerPackEvent.SetStickerPackName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is StickerPackEvent.SetStickerPackTrayIcon -> {
                _state.update { it.copy(
                    trayIcon = event.trayIcon
                ) }
            }
            is StickerPackEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingStickerPack = true
                ) }
            }
            is StickerPackEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingStickerPack = false
                ) }
            }
            is StickerPackEvent.SeedingStickers -> {
                _state.update {
                    it.copy(
                        isSeeingStickers = true
                    )
                }
            }
            is StickerPackEvent.SelectingTrayIcon -> {
                _state.update {
                    it.copy(
                        isSelectingTrayIcon = true
                    )
                }
            }
            is StickerPackEvent.DoneSeedingStickers -> {
                _state.update {
                    it.copy(
                        isSeeingStickers = false
                    )
                }
            }
            is StickerPackEvent.DoneSelectingTrayIcon -> {
                _state.update {
                    it.copy(
                        isSelectingTrayIcon = false
                    )
                }
            }
            is StickerPackEvent.ExportToWhatsApp -> {
                viewModelScope.launch {
                    val stickers = withContext(Dispatchers.IO) {
                        stickerDao.getStickersByPackSync(event.pack.packId)
                    }

                    if (stickers.size < 3) {
                        event.onResult(false, "You need at least 3 stickers in a pack")
                        return@launch
                    }

                    if (stickers.size > 30) {
                        event.onResult(false, "No more than 30 stickers allowed in a pack")
                        return@launch
                    }

                    // Ensure tray icon exists and DB is updated
                    val trayFile = File(File(event.context.filesDir, "packs/${event.pack.packId}"), "tray.webp")
                    if (event.pack.trayIcon.isEmpty() || !trayFile.exists()) {
                        if (stickers.isNotEmpty()) {
                            val firstStickerFile = File(event.context.filesDir, "packs/${stickers[0].fileName}")
                            if (firstStickerFile.exists()) {
                                withContext(Dispatchers.IO) {
                                    val trayPath = convertImageToTrayIcon(event.context, Uri.fromFile(firstStickerFile), event.pack.packId)
                                    if (trayPath != null) {
                                        dao.updateTrayIcon(event.pack.packId, trayPath)
                                    }
                                }
                            }
                        }
                    }
                    event.onResult(true, null)
                }
            }
        }
    }
}
