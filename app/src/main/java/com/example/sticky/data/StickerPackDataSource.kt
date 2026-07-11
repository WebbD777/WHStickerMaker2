package com.example.sticky.data

import com.example.sticky.model.PackModel

class StickerPackDataSource(){
    fun loadStickerPacks(): List<PackModel>{
        return listOf<PackModel>(
            PackModel("Pack 1", "Author 1"),
            PackModel("Pack 2", "Author 2"),
            PackModel("Pack 3", "Author 3")
        )
    }
}
