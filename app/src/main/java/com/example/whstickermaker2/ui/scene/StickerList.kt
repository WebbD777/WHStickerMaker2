package com.example.whstickermaker2.ui.scene

import android.content.Context
import android.net.Uri
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.whstickermaker2.utils.image.convertImageToSticker
import com.example.whstickermaker2.utils.image.getAllStickerUrisFromTestDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CenterHelloWorldScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello World")
    }
}

@Composable
fun ImageSelector(context: Context) {
    val coroutineScope = rememberCoroutineScope()

    // 1. Manage your URIs as a mutable state list so Compose tracks additions
    val uriList = remember { mutableStateListOf<Uri>() }

    // 2. Initial load of images when this screen is first opened
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val initialUris = getAllStickerUrisFromTestDirectory(context)
            withContext(Dispatchers.Main) {
                uriList.addAll(initialUris)
            }
        }
    }

    // 3. Trigger image processing inside the onResult callback, NOT the button click
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { selectedUri ->
                coroutineScope.launch(Dispatchers.IO) {
                    // Generate a unique file name so you aren't constantly overwriting "sticker2.webp"
                    val uniqueFileName = "sticker_${System.currentTimeMillis()}.webp"

                    // Convert and save image
                    convertImageToSticker(context, selectedUri, uniqueFileName)

                    // Re-fetch the updated list from the directory and push it back to the UI thread
                    val updatedUris = getAllStickerUrisFromTestDirectory(context)
                    withContext(Dispatchers.Main) {
                        uriList.clear()
                        uriList.addAll(updatedUris)
                    }
                }
            }
        }
    )

    // Layout wrapping both the Grid and the Floating Button
    Box(modifier = Modifier.fillMaxSize()) {
        StickerGridScreen(stickerUris = uriList)

        AddstickerButton(onClick = {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        })
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
fun StickerGridScreen(stickerUris: List<Uri>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Pass a unique key identifier to optimise grid item recomposition
        items(stickerUris, key = { it.toString() }) { uri ->
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