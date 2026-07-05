package com.example.whstickermaker2.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.whstickermaker2.event.StickerEvent
import com.example.whstickermaker2.event.StickerPackEvent
import com.example.whstickermaker2.model.database.dao.StickerDAO
import com.example.whstickermaker2.model.database.table.StickerTable
import com.example.whstickermaker2.state.StickerState
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
    fun onEvent(event: StickerPackEvent) {
        when (event) {
            is StickerEvent.SetStickerEmojis -> {
                _state.update { it.copy(
                    emojis = event.emojis
                ) }
            }
            is StickerEvent.SetStickerFilaName -> {
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
            StickerEvent.SaveSticker -> {
                val name = state.value.name
                val emojis = state.value.emojis
                val isAnimated = state.value.isAnimatedSticker
            }
            else -> {/*Doen niks*/}
        }
    }
}
