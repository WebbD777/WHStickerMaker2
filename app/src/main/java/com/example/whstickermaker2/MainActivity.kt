package com.example.whstickermaker2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.whstickermaker2.data.StickerPackDataSource
import com.example.whstickermaker2.ui.scene.ListStickerPacks
import com.example.whstickermaker2.ui.theme.WHStickerMaker2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WHStickerMaker2Theme {
                Scaffold(
                    topBar = { TitleBar() }
                ) { innerPadding ->
                    ListStickerPacks(
                        packInfoList = StickerPackDataSource().loadStickerPacks(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
               
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text("My App")
        },
        modifier = modifier
    )
}