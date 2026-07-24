package com.example.sticky.ui.scene

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.sticky.BuildConfig
import com.example.sticky.event.StickerPackEvent
import com.example.sticky.model.database.table.StickerPackTable
import com.example.sticky.ui.navigation.Screen
import com.example.sticky.ui.viewmodel.StickerPackViewModel
import com.example.sticky.ui.viewmodel.ViewModelFactory
import com.example.sticky.utils.image.getStickerUri

@Composable
fun StickerPackListScreen(
    navController: NavController,
    viewModel: StickerPackViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current)
    )
) {
    // Checks if db updates and reacts
    val stickerPacks by viewModel.stickerPacks.collectAsState(initial = emptyList())
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        ListStickerPacks(
            navController = navController,
            packInfoList = stickerPacks,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        if (state.isAddingStickerPack) {
            AddStickerDialog(
                onDismiss = {
                    viewModel.onEvent(StickerPackEvent.HideDialog(false))
                },
                onSave = { name ->
                    viewModel.onEvent(StickerPackEvent.SetStickerPackName(name))
                    viewModel.onEvent(StickerPackEvent.SetStickerPackCreatedAt(System.currentTimeMillis()))
                    viewModel.onEvent(StickerPackEvent.SaveStickerPack)
                }
            )
        }

        AddPackFAB(onClick = {
            viewModel.onEvent(StickerPackEvent.ShowDialog(true))
        })
    }
}

@Composable
fun StickerPackCard(pack: StickerPackTable, onCardClick: () -> Unit, viewModel: StickerPackViewModel, modifier: Modifier = Modifier){
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // WhatsApp doesn't always return Activity.RESULT_OK even on success.
        // We can check for error extras if needed, but for now we'll just let it be.
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Pack added successfully", Toast.LENGTH_SHORT).show()
        }
    }
    val trayPath by produceState(initialValue = pack.trayIcon, pack.trayIcon, pack.packId) {
        if (pack.trayIcon.isEmpty()) {
            value = viewModel.getFirstStickerPath(pack.packId) ?: ""
        }
    }
    val uri = if (trayPath.isNotEmpty()) getStickerUri(context, trayPath) else Uri.EMPTY
    Card(modifier = modifier
        .fillMaxWidth()
        .height(100.dp)
        .clickable{onCardClick()}) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    TrayIconCard(
                        uri = uri,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = pack.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            SendPackToWHButton(onClick = {
                viewModel.onEvent(StickerPackEvent.ExportToWhatsApp(pack, context) { success, message ->
                    if (success) {
                        addPackToWhatsApp(launcher, context, pack.packId, pack.name)
                    } else if (message != null) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                })
            })
        }
    }
}

@Composable
fun ListStickerPacks(
    navController: NavController,
    packInfoList: List<StickerPackTable>,
    viewModel: StickerPackViewModel,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ) {
        items(packInfoList) { pack ->
            StickerPackCard(
                pack = pack,
                modifier = Modifier.padding(8.dp),
                viewModel = viewModel,
                onCardClick = {
                  navController.navigate(Screen.StickerScreen.createRoute(pack.packId))
                }
            )
        }
    }
}

@Composable
fun AddPackFAB(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Add new sticker pack")
        }
    }
}

@Composable
fun SendPackToWHButton(onClick: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(4.dp), // Less round/sharper corners
            modifier = Modifier
                .padding(12.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Text(text = "Add/Update Pack",
                modifier = Modifier.padding(horizontal = 16.dp))

        }
    }
}

fun addPackToWhatsApp(launcher: ActivityResultLauncher<Intent>, context: Context, packId: Int, packName: String) {
    val intent = Intent().apply {
        action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
        putExtra("sticker_pack_id", packId.toString())
        putExtra("sticker_pack_authority", BuildConfig.CONTENT_PROVIDER_AUTHORITY)
        putExtra("sticker_pack_name", packName)
    }

    try {
        launcher.launch(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Pack could not be added", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun TrayIconCard(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    if (uri != Uri.EMPTY) {
        AsyncImage(
            model = uri,
            contentDescription = "Tray Icon",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "No tray icon",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}