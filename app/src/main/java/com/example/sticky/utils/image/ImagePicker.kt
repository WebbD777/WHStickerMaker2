package com.example.sticky.utils.image

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ImagePicker(private val activity: AppCompatActivity) {

    // 1. Register the native picker contract
    private val pickMedia = activity.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Process the image path directly
            processSticker(uri)
        }
    }

    // 2. Call this plain function from anywhere in your business logic
    fun launchSystemPicker() {
        pickMedia.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun processSticker(uri: Uri) {
        // Your sticker conversion logic
    }
}
