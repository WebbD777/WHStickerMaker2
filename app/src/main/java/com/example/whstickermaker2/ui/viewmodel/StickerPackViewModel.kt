package com.example.whstickermaker2.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whstickermaker2.utils.image.convertImageToSticker
import com.example.whstickermaker2.utils.image.getAllStickerUrisFromTestDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StickerPackViewModel(application: Application) : AndroidViewModel(application) {

    private val _stickers = MutableStateFlow<List<Uri>>(emptyList())
    val stickers: StateFlow<List<Uri>> = _stickers.asStateFlow()

    fun loadStickers() {
        viewModelScope.launch {
            val uris = withContext(Dispatchers.IO) {
                getAllStickerUrisFromTestDirectory(getApplication())
            }
            _stickers.value = uris
        }
    }

    fun addSticker(sourceUri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val uniqueFileName = "sticker_${System.currentTimeMillis()}.webp"
                convertImageToSticker(getApplication(), sourceUri, uniqueFileName)
                loadStickers()
            }
        }
    }
}
