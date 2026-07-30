package com.example.sticky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sticky.ui.scene.ImageSelector
import com.example.sticky.ui.scene.StickerPackListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StickerPackScreen.route) {
        composable(route = Screen.StickerPackScreen.route) {
            StickerPackListScreen(navController = navController)
        }
        composable(
            route = Screen.StickerScreen.route,
            arguments = listOf(navArgument("packId") { type = NavType.IntType })
        ) { backStackEntry ->
            val packId = backStackEntry.arguments?.getInt("packId") ?: -1
            ImageSelector(packId = packId)
        }
        composable(Screen.ImagePickerScreen.route) {
        //    CenterHelloWorldScreen(navController)
            // Test navigation
        }
    }
}
