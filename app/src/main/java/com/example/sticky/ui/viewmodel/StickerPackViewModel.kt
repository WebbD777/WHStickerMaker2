package com.example.sticky.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sticky.event.StickerPackEvent
import com.example.sticky.model.database.dao.StickerPackDAO
import com.example.sticky.model.database.table.StickerPackTable
import com.example.sticky.state.StickerPackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StickerPackViewModel(
    private val dao : StickerPackDAO
) : ViewModel(){

    // handles user input like name, author, etc
    private val _state = MutableStateFlow(StickerPackState())
    // handles the list from the database
    val stickerPacks = dao.getAllStickerPacks()

    val state = _state.asStateFlow()
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
                    isAnimated = true,
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
        }
    }
}
