package com.example.sticky.ui.scene

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sticky.event.StickerPackEvent
import com.example.sticky.model.database.table.StickerPackTable
import com.example.sticky.ui.navigation.Screen
import com.example.sticky.ui.viewmodel.StickerPackViewModel
import com.example.sticky.ui.viewmodel.ViewModelFactory

@Composable
fun StickerPackListScreen(
    navController: NavController,
    viewModel: StickerPackViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current)
    )
) {
    // Checks if db updates and reacts
    val stickerPacks by viewModel.stickerPacks.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        ListStickerPacks(
            navController = navController,
            packInfoList = stickerPacks,
            modifier = Modifier.fillMaxSize()
        )

        AddPackFAB(onClick = {
            viewModel.onEvent(StickerPackEvent.ShowDialog(true))
        })
    }
}

@Composable
fun StickerPackCard(pack: StickerPackTable, onCardClick: () -> Unit, modifier: Modifier = Modifier){
    Card(modifier = modifier.clickable{onCardClick()}) {
        Column {
            Text(
                text = pack.name,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun ListStickerPacks(
    navController: NavController,
    packInfoList: List<StickerPackTable>,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ) {
        items(packInfoList) { pack ->
            StickerPackCard(
                pack = pack,
                modifier = Modifier.padding(8.dp),
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
            onClick = { onClick() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Add new sticker pack")
        }
    }
}
