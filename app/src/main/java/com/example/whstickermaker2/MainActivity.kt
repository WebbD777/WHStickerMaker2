package com.example.whstickermaker2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.whstickermaker2.data.StickerPackDataSource
import com.example.whstickermaker2.ui.scene.ListStickerPacks
import com.example.whstickermaker2.ui.theme.WHStickerMaker2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WHStickerMaker2Theme {
                ListStickerPacks(
                    packInfoList = StickerPackDataSource().loadStickerPacks(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}