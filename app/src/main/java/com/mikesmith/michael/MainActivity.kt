package com.mikesmith.michael

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikesmith.michael.ui.theme.MichaelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content(
                applicationContext?.let {
                    val displayMetrics = it.resources.displayMetrics
                    displayMetrics.widthPixels / displayMetrics.density
                }?.dp ?: 0.dp
            )
        }
    }
}

@Composable
fun Content(width: Dp) {
    MichaelTheme {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold {
                GameScreen(screenWidth = width)
            }
        }
    }
}
