package com.mikesmith.michael.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class KeyboardKeyState {
    NORMAL,
    GREEN,
    YELLOW,
    CROSSED
}

data class KeyboardKeyData(
    val letter: Char,
    val state: KeyboardKeyState,
)

private val keyboardFirstLine =
    "QWERTYUIOP".toCharArray().map { KeyboardKeyData(it, KeyboardKeyState.NORMAL) }
private val keyboardSecondLine =
    "ASDFGHJKL".toCharArray().map { KeyboardKeyData(it, KeyboardKeyState.NORMAL) }
private val keyboardThirdLine = "ZXCVBNM".toCharArray().map { KeyboardKeyData(it, KeyboardKeyState.NORMAL) }
val keyList = listOf(keyboardFirstLine, keyboardSecondLine, keyboardThirdLine)

@Composable
fun Keyboard(
    keyboardTileWidth: Float,
    keyboardTileHeight: Float,
    enterEnabled: Boolean,
    keyboardRows: List<List<KeyboardKeyData>>,
    onTileClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit,
) {

    keyboardRows.forEach { keyboardRow ->
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            keyboardRow.map {
                KeyboardKey(
                    keyboardTileWidth = keyboardTileWidth,
                    keyboardTileHeight = keyboardTileHeight,
                    keyData = it,
                    onTileClick = onTileClick
                )
            }
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
    keyData: KeyboardKeyData,
    onTileClick: (Char) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(2.dp)
            .padding(1.dp)
            .width(keyboardTileWidth.dp)
            .height(keyboardTileHeight.dp)
            .clickable { onTileClick(keyData.letter) }
            .then(
                when (keyData.state) {
                    KeyboardKeyState.NORMAL -> Modifier
                    KeyboardKeyState.GREEN -> Modifier.background(Green)
                    KeyboardKeyState.YELLOW -> Modifier.background(Yellow)
                    KeyboardKeyState.CROSSED -> Modifier
                }
            )
    ) {
        if (keyData.state == KeyboardKeyState.CROSSED) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawLine(
                    start = Offset(x = canvasWidth, y = 0f),
                    end = Offset(x = 0f, y = canvasHeight),
                    strokeWidth = 5f,
                    color = Red
                )
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = canvasWidth, y = canvasHeight),
                    strokeWidth = 5f,
                    color = Red
                )
            }
        }

        Text(
            text = keyData.letter.toString(),
            fontSize = 16.sp,
            color = when (keyData.state) {
                KeyboardKeyState.NORMAL -> White
                KeyboardKeyState.GREEN -> Black
                KeyboardKeyState.YELLOW -> Black
                KeyboardKeyState.CROSSED -> Gray
            }
        )
    }
}