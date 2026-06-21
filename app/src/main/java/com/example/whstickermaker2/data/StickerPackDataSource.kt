package com.example.whstickermaker2.data

import com.example.whstickermaker2.model.PackModel

class StickerPackDataSource(){
    fun loadStickerPacks(): List<PackModel>{
        return listOf<PackModel>(
            PackModel("Pack 1", "Author 1"),
            PackModel("Pack 2", "Author 2"),
            PackModel("Pack 3", "Author 3")
        )
    }
}
