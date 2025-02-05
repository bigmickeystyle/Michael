package com.mikesmith.michael

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikesmith.michael.ui.theme.MichaelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val dictionary by lazy {
        assets.open("words.txt").bufferedReader().use { it.readText() }.split("\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content(
                width = applicationContext?.let {
                    val displayMetrics = it.resources.displayMetrics
                    displayMetrics.widthPixels / displayMetrics.density
                }?.dp ?: 0.dp,
                dictionary = dictionary
            )
        }
    }
}

@Composable
fun Content(width: Dp, dictionary: List<String>) {
    MichaelTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold { contentPadding ->
                GameScreen(
                    screenWidth = width,
                    contentPadding = contentPadding,
                    dictionary = dictionary
                )
            }
        }
    }
}
