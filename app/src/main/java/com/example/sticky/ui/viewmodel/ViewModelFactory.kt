package com.example.sticky.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sticky.data.DatabaseProvider

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = DatabaseProvider.getDatabase(context)
        return when {
            modelClass.isAssignableFrom(StickerViewModel::class.java) -> {
                StickerViewModel(db.stickerDao, db.stickerPackDao) as T
            }
            modelClass.isAssignableFrom(StickerPackViewModel::class.java) -> {
                StickerPackViewModel(db.stickerPackDao, db.stickerDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}