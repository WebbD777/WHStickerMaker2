package com.example.sticky.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sticky.event.StickerEvent
import com.example.sticky.model.database.dao.StickerDAO
import com.example.sticky.model.database.dao.StickerPackDAO
import com.example.sticky.model.database.table.StickerTable
import com.example.sticky.state.StickerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StickerViewModel(
    private val dao : StickerDAO,
    private val packDao: StickerPackDAO
) : ViewModel() {
    // handles user input like name, author, etc
    private val _state = MutableStateFlow(StickerState())

    private val _packId = MutableStateFlow<Int>(-1)

    // handles the list from the database
    @OptIn(ExperimentalCoroutinesApi::class)
    val stickers = _packId.flatMapLatest { id ->
        dao.getStickersByPack(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val state = _state.asStateFlow()

    fun setPackId(id: Int) {
        _packId.value = id
    }

//    fun addSticker(sticker: StickerTable) {
//        viewModelScope.launch {
//            dao.insertSticker(sticker)
//        }
//    }

    fun onEvent(event: StickerEvent) {
        when (event) {
            is StickerEvent.SetStickerEmojis -> {
                _state.update { it.copy(
                    emojis = event.emojis
                ) }
            }
            is StickerEvent.SetStickerFileName -> {
                _state.update { it.copy(
                    name = event.name
                ) }
            }
            is StickerEvent.SetStickerIsAnimated -> {
                _state.update {
                    it.copy(
                        isAnimatedSticker = event.isAnimated
                    )
                }
            }
            is StickerEvent.SelectingImage -> {
                _state.update {
                    it.copy(
                        isSelectingImage = event.isSelectingImage
                    )
                }
            }
            is StickerEvent.ImageSelected -> {
                _state.update {
                    it.copy(
                        isSelectingImage = false,
                        imageUri = event.imageUri
                    )
                }
            }
            StickerEvent.SaveSticker -> {
                val name = state.value.name
                val emojis = state.value.emojis
                val isAnimated = state.value.isAnimatedSticker
                val packId = _packId.value
                
                if (packId != -1) {
                    viewModelScope.launch {
                        // 1. Insert the new sticker
                        dao.insertSticker(
                            StickerTable(
                                packId = packId,
                                fileName = name,
                                emojis = emojis,
                                isAnimated = isAnimated
                            )
                        )
                        // 2. Increment the imageDataVersion of the pack and update tray icon if empty
                        packDao.getStickerPack(packId)?.let { pack ->
                            val newTrayIcon = if (pack.trayIcon.isEmpty()) "$packId/tray.webp" else pack.trayIcon
                            packDao.insertStickerPack(
                                pack.copy(
                                    imageDataVersion = pack.imageDataVersion + 1,
                                    trayIcon = newTrayIcon
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
