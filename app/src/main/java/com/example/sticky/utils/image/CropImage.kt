package com.example.sticky.utils.image

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView

@Composable
fun CanHubCropView(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    onImageViewCreated: (CropImageView) -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CropImageView(context).apply {
                setImageUriAsync(imageUri)
                onImageViewCreated(this)
            }
        },
        update = { view ->
            // Update the URI if it changes during recomposition
            view.setImageUriAsync(imageUri)
        },
    )
}
