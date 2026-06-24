package com.example.whstickermaker2.ui.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CenterHelloWorldScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(), // Fills the entire available screen
        contentAlignment = Alignment.Center // Centers all content within the Box
    ) {
        Text(text = "Hello World")
    }
}