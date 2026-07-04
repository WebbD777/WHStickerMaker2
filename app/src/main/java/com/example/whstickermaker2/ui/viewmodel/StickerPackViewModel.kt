package com.example.whstickermaker2.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.whstickermaker2.event.StickerPackEvent
import com.example.whstickermaker2.model.database.dao.StickerPackDAO
import com.example.whstickermaker2.state.StickerPackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StickerPackViewModel(
    private val dao : StickerPackDAO
) : ViewModel(){

    // handles user input like name, author, etc
    private val _state = MutableStateFlow(StickerPackState())
    // handles the list from the database
    private val _stickerPacks = dao.getAllStickerPacks()

    val state = _state.asStateFlow()
    fun onEvent(event: StickerPackEvent){
        when(event){
            StickerPackEvent.SaveStickerPack -> {
                val name = state.value.name
                val author = state.value.author
                val trayIcon = state.value.trayIcon
                val imageDataVersion = state.value.imageDataVersion
                val isAnimated = state.value.isAnimated
                val createdAt = state.value.createdAt
            }
            is StickerPackEvent.SetStickerPackAuthor -> {
                _state.update { it.copy(
                    author = event.author
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
            else -> {/*Doen niks*/}
        }
    }
}
