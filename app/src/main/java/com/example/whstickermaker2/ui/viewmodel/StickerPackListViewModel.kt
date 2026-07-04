package com.example.whstickermaker2.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.whstickermaker2.data.StickerPackDataSource
import com.example.whstickermaker2.model.PackModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StickerPackListViewModel : ViewModel() {
    private val _stickerPacks = MutableStateFlow<List<PackModel>>(emptyList())
    val stickerPacks: StateFlow<List<PackModel>> = _stickerPacks.asStateFlow()

    init {
        loadPacks()
    }

    fun loadPacks() {
        _stickerPacks.value = StickerPackDataSource().loadStickerPacks()
    }
}
