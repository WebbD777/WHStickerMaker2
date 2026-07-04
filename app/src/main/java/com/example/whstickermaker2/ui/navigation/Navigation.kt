package com.example.whstickermaker2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whstickermaker2.ui.scene.CenterHelloWorldScreen
import com.example.whstickermaker2.ui.scene.ImageSelector
import com.example.whstickermaker2.ui.scene.StickerPackListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StickerPackScreen.route) {
        composable(route = Screen.StickerPackScreen.route) {
            StickerPackListScreen(navController = navController)
        }
        composable(Screen.StickerScreen.route) {
            ImageSelector()
        }
        composable(Screen.ImagePickerScreen.route) {
            CenterHelloWorldScreen(navController)
        }
    }
}