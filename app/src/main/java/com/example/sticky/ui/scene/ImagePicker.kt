package com.example.sticky.ui.scene

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import android.net.Uri

@Composable
fun ImagePickerScreen(onImageSelected: (Uri) -> Unit) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Registers the photo picker activity launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // This callback executes after the user selects an image or cancels
        if (uri != null) {
            selectedImageUri = uri
            onImageSelected(uri)
        }
    }

    Column {
        Button(onClick = {
            // Launch the picker, filtering for images only
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Select a Photo")
        }

        if (selectedImageUri != null) {
            Text("Selected Image: ${selectedImageUri.toString()}")
        }
    }
}
