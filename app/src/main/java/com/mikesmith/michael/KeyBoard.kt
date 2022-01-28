package com.mikesmith.michael

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Keyboard(keyboardTileWidth: Float, keyboardTileHeight: Float) {
    val keyboardFirstLine = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    val keyboardSecondLine = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    val keyboardThirdLine = listOf("Z", "X", "C", "V", "B", "N", "M")

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        keyboardFirstLine.map {
            KeyboardKey(
                keyboardTileWidth = keyboardTileWidth,
                keyboardTileHeight = keyboardTileHeight,
                text = it,
            ) {}
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
            ) {}
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
            ) {}
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
    text: String,
    onTileClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(2.dp)
            .padding(1.dp)
            .width(keyboardTileWidth.dp)
            .height(keyboardTileHeight.dp)
            .clickable { onTileClick() }
    ) {
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}