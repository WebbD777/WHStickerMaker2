package com.example.whstickermaker2.ui.navigation

sealed class Screen(val route: String) {
    object StickerPackScreen : Screen("sticker_pack")
    object StickerScreen : Screen("sticker")
}