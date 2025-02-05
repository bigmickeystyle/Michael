package com.mikesmith.michael.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikesmith.michael.MichaelClickData
import com.mikesmith.michael.Tile
import com.mikesmith.michael.TileState
import com.mikesmith.michael.ui.theme.Gold

@Composable
fun LetterTile(
    text: String,
    backGround: Color,
    tryPosition: Int,
    wordPosition: Int,
    tileState: TileState,
    size: Dp,
    fontSize: TextUnit,
    isDisabled: Boolean,
    onTileClick: (MichaelClickData) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .then(
                if (isDisabled) Modifier.border(BorderStroke(2.dp, Color.White)) else Modifier
            )
            .background(backGround)
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
    shouldDisable: Boolean = false,
    onTileClick: (MichaelClickData) -> Unit = { },
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        tiles.mapIndexed { index, tile ->
            val backGround = when (tile.tileState) {
                TileState.GUESSING -> Color.LightGray
                TileState.NO_MATCH -> Color.Black
                TileState.GOOD_BUT_NOT_RIGHT -> Gold
                TileState.RIGHT -> Color.Green
                else -> MaterialTheme.colorScheme.secondary
            }

            val letterText =
                if (shouldDisable) "" else tile.character?.uppercaseChar()?.toString() ?: ""

            LetterTile(
                text = letterText,
                backGround = backGround,
                tryPosition = tryPosition,
                wordPosition = index,
                tileState = tile.tileState,
                size = size,
                fontSize = fontSize,
                isDisabled = shouldDisable,
                onTileClick
            )
        }
    }
}

@Preview
@Composable
fun PreviewLetterRow() {
    LetterRow(
        tiles = listOf(
        Tile(tileState = TileState.RIGHT, "H".first()),
        Tile(tileState = TileState.RIGHT, "H".first()),
        Tile(tileState = TileState.RIGHT, "H".first()),
        Tile(tileState = TileState.RIGHT, "H".first()),
    ), tryPosition = 0, size = 30.dp, fontSize = 16.sp
    )
}
