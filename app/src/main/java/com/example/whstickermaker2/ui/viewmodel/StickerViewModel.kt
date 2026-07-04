package com.example.whstickermaker2.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whstickermaker2.data.StickerPackDataSource
import com.example.whstickermaker2.model.PackModel
import com.example.whstickermaker2.utils.image.convertImageToSticker
import com.example.whstickermaker2.utils.image.getAllStickerUrisFromTestDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StickerViewModel(application: Application) : AndroidViewModel(application) {

    private val stickerPackDataSource = StickerPackDataSource()

    private val _stickerPacks = MutableStateFlow<List<PackModel>>(emptyList())
    val stickerPacks: StateFlow<List<PackModel>> = _stickerPacks.asStateFlow()

    private val _currentPackStickers = MutableStateFlow<List<Uri>>(emptyList())
    val currentPackStickers: StateFlow<List<Uri>> = _currentPackStickers.asStateFlow()

    init {
        loadPacks()
    }

    private fun loadPacks() {
        viewModelScope.launch {
            val packs = withContext(Dispatchers.IO) {
                stickerPackDataSource.loadStickerPacks()
            }
            _stickerPacks.value = packs
        }
    }

    fun loadStickers() {
        viewModelScope.launch {
            val uris = withContext(Dispatchers.IO) {
                getAllStickerUrisFromTestDirectory(getApplication())
            }
            _currentPackStickers.value = uris
        }
    }

    fun addSticker(sourceUri: Uri) {
        viewModelScope.launch {
            val newStickerUri = withContext(Dispatchers.IO) {
                val context = getApplication<Application>()
                val uniqueFileName = "sticker_${System.currentTimeMillis()}.webp"
                val stickerFile = convertImageToSticker(context, sourceUri, uniqueFileName)
                
                stickerFile?.let { file ->
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                }
            }

            if (newStickerUri != null) {
                // Efficiently append the new sticker instead of re-scanning the whole directory
                _currentPackStickers.update { currentList ->
                    currentList + newStickerUri
                }
            }
        }
    }
}
