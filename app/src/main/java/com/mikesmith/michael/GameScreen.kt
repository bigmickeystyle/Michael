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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import timber.log.Timber

@Composable
fun GameScreen(viewModel: MichaelViewModel = viewModel(), screenWidth: Dp) {

    val tileSize = ((screenWidth.value - 40f) / 5) - 12

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
            when (viewModel.gameState) {
                is MichaelState.Playing -> EmptyGame(tileSize.dp) { text ->
                    viewModel.onTileClick(text)
                }
                MichaelState.Start -> EmptyGame(tileSize.dp) { text ->
                    viewModel.onTileClick(text)
                }
            }

        }
    }
}

@Composable
fun EmptyGame(tileSize: Dp, onTileClick: (String) -> Unit) {
    Column {
        LetterRow(
            listOf(
                Tile("C", TileState.GUESSING),
                Tile("L", TileState.GUESSING),
                Tile("O", TileState.GUESSING),
                Tile("N", TileState.GUESSING),
                Tile("E", TileState.GUESSING),
            ),
            tileSize,
            onTileClick
        )
        LetterRow(
            listOf(
                Tile("C", TileState.GUESSING),
                Tile("L", TileState.GUESSING),
                Tile("O", TileState.GUESSING),
                Tile("N", TileState.GUESSING),
                Tile("E", TileState.GUESSING),
            ),
            tileSize,
            onTileClick
        )
        LetterRow(
            listOf(
                Tile("C", TileState.GUESSING),
                Tile("L", TileState.GUESSING),
                Tile("O", TileState.GUESSING),
                Tile("N", TileState.GUESSING),
                Tile("E", TileState.GUESSING),
            ),
            tileSize,
            onTileClick
        )
        LetterRow(
            listOf(
                Tile("C", TileState.GUESSING),
                Tile("L", TileState.GUESSING),
                Tile("O", TileState.GUESSING),
                Tile("N", TileState.GUESSING),
                Tile("E", TileState.GUESSING),
            ),
            tileSize,
            onTileClick
        )
        LetterRow(
            listOf(
                Tile("C", TileState.GUESSING),
                Tile("L", TileState.GUESSING),
                Tile("O", TileState.GUESSING),
                Tile("N", TileState.GUESSING),
                Tile("E", TileState.GUESSING),
            ),
            tileSize,
            onTileClick
        )
    }
}
