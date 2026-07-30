package com.example.sticky.ui.scene

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.sticky.event.StickerEvent
import com.example.sticky.model.database.table.StickerTable
import com.example.sticky.utils.image.convertImageToSticker
import com.example.sticky.utils.image.getStickerUri
import com.example.sticky.ui.viewmodel.StickerViewModel
import com.example.sticky.ui.viewmodel.ViewModelFactory
import com.example.sticky.utils.image.CanHubCropView

//@Composable
//fun CenterHelloWorldScreen(navController: NavController) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Hello World")
//    }
//}

@Composable
fun ImageSelector(
    packId: Int = -1, // Added packId parameter
    viewModel: StickerViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current

    val stickers by viewModel.stickers.collectAsState(initial = emptyList())
    val state by viewModel.state.collectAsState()

    // Load stickers when the screen enters the composition
    LaunchedEffect(packId) {
        viewModel.setPackId(packId)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onEvent(StickerEvent.ImageSelected(uri))
            } else {
                viewModel.onEvent(StickerEvent.SelectingImage(false))
            }
        }
    )

    // Layout wrapping both the Grid and the Floating Button
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.imageUri != Uri.EMPTY) {
            // Crop
            Box(modifier = Modifier.fillMaxSize()) {
                var cropper by remember { mutableStateOf<com.canhub.cropper.CropImageView?>(null) }

                CanHubCropView(
                    imageUri = state.imageUri,
                    modifier = Modifier.fillMaxSize()
                ) { view ->
                    cropper = view
                    view.setOnCropImageCompleteListener { _, result ->
                        if (result.isSuccessful) {
                            val croppedUri = result.uriContent
                            val relativePath = convertImageToSticker(context, croppedUri, packId, stickers.size)
                            if (relativePath != null) {
                                // If this is the first sticker, also create a proper 96x96 tray icon
                                if (stickers.isEmpty()) {
                                    com.example.sticky.utils.image.convertImageToTrayIcon(context, croppedUri, packId)
                                }
                                viewModel.onEvent(StickerEvent.SetStickerFileName(relativePath))
                                viewModel.onEvent(StickerEvent.SetStickerEmojis("☕")) // WhatsApp requires at least 1 emoji
                                viewModel.onEvent(StickerEvent.SetStickerIsAnimated(false))
                                viewModel.onEvent(StickerEvent.SaveSticker)
                            }
                            // Return to grid
                            viewModel.onEvent(StickerEvent.ImageSelected(Uri.EMPTY))
                        } else {
                            viewModel.onEvent(StickerEvent.ImageSelected(Uri.EMPTY))
                        }
                    }
                }

                // Save button for the cropper
                FloatingActionButton(
                    onClick = { cropper?.croppedImageAsync() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Check, "Save Sticker")
                }
            }
        } else {
            // --- GRID MODE ---
            StickerGridScreen(stickers = stickers)

            AddstickerButton(onClick = {
                if (stickers.size < 30) {
                    viewModel.onEvent(StickerEvent.SelectingImage(true))
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    Toast.makeText(context, "Cannot have more than 30 stickers", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

@Composable
fun StickerCard(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = uri,
        contentDescription = "Sticker preview",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun StickerGridScreen(stickers: List<StickerTable>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(stickers, key = { it.id }) { sticker ->
            val uri = remember(sticker.fileName) {
                getStickerUri(context, sticker.fileName)
            }
            
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                StickerCard(
                    uri = uri,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

@Composable
fun AddstickerButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { onClick() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Add new sticker")
        }
    }
}

