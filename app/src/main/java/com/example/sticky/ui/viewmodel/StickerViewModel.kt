package com.example.sticky.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sticky.event.StickerEvent
import com.example.sticky.model.database.dao.StickerDAO
import com.example.sticky.state.StickerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StickerViewModel(
    private val dao : StickerDAO
) : ViewModel() {
    // handles user input like name, author, etc
    private val _state = MutableStateFlow(StickerState())

    // handles the list from the database
    private val _stickerPacks = dao.getAllStickers()

    val state = _state.asStateFlow()
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
                        isSelectingImage = true
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
            }
        }
    }
}
