package com.mikesmith.michael

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LetterTile(
    text: String,
    size: Dp,
    onTileClick: (String) -> Unit,
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .background(MaterialTheme.colors.secondary)
            .padding(2.dp)
            .width(size)
            .height(size)
            .clickable { onTileClick(text) }
    ) {
        Text(
            text = text,
            fontSize = 32.sp
        )
    }

}

@Composable
fun LetterRow(tiles: List<Tile>, size: Dp, onTileClick: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth()
    ) {
        tiles.forEach {
            LetterTile(text = it.character, size = size, onTileClick)
        }
    }
}
