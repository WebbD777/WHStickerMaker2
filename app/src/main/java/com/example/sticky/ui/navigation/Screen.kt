package com.example.sticky.ui.navigation

sealed class Screen(val route: String) {
    object StickerPackScreen : Screen("sticker_pack")
    object StickerScreen : Screen("sticker/{packId}") {
        fun createRoute(packId: Int) = "sticker/$packId"
    }
    object ImagePickerScreen : Screen("image_picker")
}
