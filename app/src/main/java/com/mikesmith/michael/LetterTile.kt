package com.mikesmith.michael

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun LetterTile(
    text: String,
    backGround: Color,
    tryPosition: Int,
    wordPosition: Int,
    tileState: TileState,
    size: Dp,
    fontSize: TextUnit,
    onTileClick: (MichaelClickData) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .background(backGround)
            .padding(2.dp)
            .width(size)
            .height(size)
            .clickable { onTileClick(MichaelClickData(tryPosition, wordPosition, tileState)) }
    ) {
        Text(
            text = text,
            fontSize = fontSize
        )
    }
}

@Composable
fun LetterRow(
    tiles: List<Tile>,
    tryPosition: Int,
    size: Dp,
    fontSize: TextUnit,
    onTileClick: (MichaelClickData) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth()
    ) {
        tiles.mapIndexed { index, tile ->
            val backGround = when (tile.tileState) {
                TileState.GUESSING -> Color.LightGray
                else -> MaterialTheme.colors.secondary
            }

            LetterTile(
                text = tile.character?.uppercaseChar()?.toString() ?: "",
                backGround = backGround,
                tryPosition = tryPosition,
                wordPosition = index,
                tileState = tile.tileState,
                size = size,
                fontSize = fontSize,
                onTileClick
            )
        }
    }
}
