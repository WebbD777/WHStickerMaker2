package com.example.whstickermaker2.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whstickermaker2.data.StickerPackDataSource
import com.example.whstickermaker2.ui.scene.CenterHelloWorldScreen
import com.example.whstickermaker2.ui.scene.ListStickerPacks
import com.example.whstickermaker2.ui.scene.SmallExample

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StickerPackScreen.route) {
        composable(route = Screen.StickerPackScreen.route) {
            ListStickerPacks(
                navController = navController,
                packInfoList = StickerPackDataSource().loadStickerPacks(),
                modifier = Modifier
                    .fillMaxSize()
            )

            SmallExample(onClick = {})
        }
        composable(Screen.StickerScreen.route) {
            CenterHelloWorldScreen(navController)
        }
    }
}