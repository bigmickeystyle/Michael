package com.mikesmith.michael

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val keyboardFirstLine = "QWERTYUIOP".toCharArray()
val keyboardSecondLine = "ASDFGHJKL".toCharArray()
val keyboardThirdLine = "ZXCVBNM".toCharArray()

@Composable
fun Keyboard(
    keyboardTileWidth: Float,
    keyboardTileHeight: Float,
    enterEnabled: Boolean,
    onTileClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keyboardFirstLine.map {
            KeyboardKey(
                keyboardTileWidth = keyboardTileWidth,
                keyboardTileHeight = keyboardTileHeight,
                text = it,
                onTileClick = onTileClick
            )
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keyboardSecondLine.map {
            KeyboardKey(
                keyboardTileWidth = keyboardTileWidth,
                keyboardTileHeight = keyboardTileHeight,
                text = it,
                onTileClick = onTileClick
            )
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keyboardThirdLine.map {
            KeyboardKey(
                keyboardTileWidth = keyboardTileWidth,
                keyboardTileHeight = keyboardTileHeight,
                text = it,
                onTileClick = onTileClick
            )
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(2.dp)
                .padding(1.dp)
                .width(keyboardTileWidth.dp * 4)
                .height(keyboardTileHeight.dp)
                .clickable { if (enterEnabled) onEnterClick() else Unit }
        ) {
            Text(
                text = "ENTER",
                fontSize = 16.sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(2.dp)
                .padding(1.dp)
                .width(keyboardTileWidth.dp * 4)
                .height(keyboardTileHeight.dp)
                .clickable { onDeleteClick() }
        ) {
            Text(
                text = "<- DEL",
                fontSize = 16.sp
            )
        }
    }

}

@Composable
fun KeyboardKey(
    keyboardTileWidth: Float,
    keyboardTileHeight: Float,
    text: Char,
    onTileClick: (Char) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(2.dp)
            .padding(1.dp)
            .width(keyboardTileWidth.dp)
            .height(keyboardTileHeight.dp)
            .clickable { onTileClick(text) }
    ) {
        Text(
            text = text.toString(),
            fontSize = 16.sp
        )
    }
}