package com.example.sticky.ui.navigation

sealed class Screen(val route: String) {
    object StickerPackScreen : Screen("sticker_pack")
    object StickerScreen : Screen("sticker")
    object ImagePickerScreen : Screen("image_picker")
}