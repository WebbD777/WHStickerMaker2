package com.example.sticky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sticky.ui.navigation.Navigation
import com.example.sticky.ui.theme.Sticky

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sticky {
                Scaffold(
                    topBar = { TitleBar() }
                ) { innerPadding -> // 1. Capture the padding values
                    Box(modifier = Modifier.padding(innerPadding)) { // 2. Apply them
                        Navigation()
                    }
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
            Text("Sticky")
        },
        modifier = modifier
    )
}
