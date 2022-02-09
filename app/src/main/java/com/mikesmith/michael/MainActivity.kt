package com.mikesmith.michael

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mikesmith.michael.ui.theme.MichaelTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val dictionary by lazy { assets.open("words.txt").bufferedReader().use { it.readText() }.split("\n") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
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
        Surface(color = MaterialTheme.colors.background) {
            Scaffold {
                GameScreen(screenWidth = width, dictionary = dictionary)
            }
        }
    }
}
