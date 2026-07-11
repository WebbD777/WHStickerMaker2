package com.example.sticky.data

import com.example.sticky.model.PackModel
import com.example.sticky.model.database.dao.StickerPackDAO
import com.example.sticky.model.database.table.StickerPackTable

class StickerPackDataSource(
    private val dao: StickerPackDAO
){
    fun getAllPacks() = dao.getAllStickerPacks()

    suspend fun savePack(pack: StickerPackTable) = dao.insertStickerPack(pack)
}

//class StickerPackDataSource(){
//    fun loadStickerPacks(): List<PackModel>{
//        return listOf<PackModel>(
//            PackModel("Pack 1", "Author 1"),
//            PackModel("Pack 2", "Author 2"),
//            PackModel("Pack 3", "Author 3")
//        )
//    }
//}
