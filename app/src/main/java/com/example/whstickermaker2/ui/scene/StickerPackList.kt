package com.example.whstickermaker2.ui.scene

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.whstickermaker2.model.PackModel
import com.example.whstickermaker2.ui.navigation.Screen

@Composable
fun StickerPackCard(packModel: PackModel, onCardClick: () -> Unit, modifier: Modifier = Modifier){
    Card(modifier = modifier.clickable{onCardClick()}) {
        Column {
            Text(
                text = packModel.name,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = packModel.author,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun ListStickerPacks(
    navController: NavController,
    packInfoList: List<PackModel>,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ) {
        items(packInfoList) { packModel ->
            StickerPackCard(
                packModel = packModel,
                modifier = Modifier.padding(8.dp),
                onCardClick = {
                  navController.navigate(Screen.StickerScreen.route)
                }
            )
        }
    }
}

@Composable
fun SmallExample(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adds a nice margin from the screen edges
        contentAlignment = Alignment.BottomEnd // Aligns content to the bottom right
    ) {
        FloatingActionButton(
            onClick = { onClick() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Small floating action button.")
        }
    }
}
