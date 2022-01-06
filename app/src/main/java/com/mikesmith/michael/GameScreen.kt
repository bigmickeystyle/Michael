package com.mikesmith.michael

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import timber.log.Timber

@Composable
fun GameScreen(viewModel: MichaelViewModel = viewModel(), screenWidth: Dp) {

    fun tileSize() = ((screenWidth.value - 40f) / viewModel.gameState.word.length) - 12
    fun fontSize() = 200 / viewModel.gameState.word.length

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            NavButton()
            Text(
                text = "Michael",
                fontSize = 45.sp
            )
            NavButton()
        }
        Row {
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
        Row(Modifier.padding(20.dp)) {
            val currentTileSize = tileSize()
            val fontSize = fontSize()

            when (viewModel.gameState) {
                is MichaelState.Playing -> EmptyGame(
                    viewModel.gameState.word,
                    currentTileSize.dp,
                    fontSize.sp,
                    ) { text -> viewModel.onTileClick(text) }
                MichaelState.Start -> EmptyGame(
                    viewModel.gameState.word,
                    currentTileSize.dp,
                    fontSize.sp,
                    ) { text -> viewModel.onTileClick(text) }
            }
        }
    }
}

@Composable
fun EmptyGame(word: String, tileSize: Dp, fontSize: TextUnit, onTileClick: (String) -> Unit) {
    Column {
        repeat(word.length) {
            LetterRow(
                word.map {
                    Tile(it, TileState.GUESSING)
                },
                tileSize,
                fontSize,
                onTileClick
            )
        }
    }
}
