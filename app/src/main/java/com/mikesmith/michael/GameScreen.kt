package com.mikesmith.michael

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

private val initalWord = "MICHAEL"

@Composable
fun GameScreen(viewModel: MichaelViewModel = viewModel(), screenWidth: Dp) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun tileSize(word: String) = ((screenWidth.value - 40f) / word.length) - 12
    fun tileFontSize(word: String) = 200 / word.length
    fun keyboardTileWidth() = ((screenWidth.value - 20f) / 10) - 6
    fun keyboardTileHeight() = ((screenWidth.value - 20f) / 6) - 6

    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                NavButton()
                Box(modifier = Modifier.clickable {
                    viewModel.onRestartClick()
                }) {
                    Text(
                        text = "Michael",
                        fontSize = 45.sp
                    )
                }
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

                when (val state = viewModel.gameState) {
                    is MichaelState.Loading -> Box {
                        Text(text = "loading")
                    }
                    is MichaelState.Won -> Column {
                        state.tileRows.mapIndexed { index, tileRow ->
                            LetterRow(
                                tiles = tileRow.tiles,
                                tryPosition = index,
                                size = tileSize(state.word).dp,
                                fontSize = tileFontSize(state.word).sp,
                                shouldDisable = true
                            )
                        }
                    }
                    is MichaelState.Lost -> Box {
                        Text(text = "Lost")
                    }
                    is MichaelState.Playing -> Column {
                        state.tileRows.mapIndexed { index, tileRow ->
                            LetterRow(
                                tiles = tileRow.tiles,
                                tryPosition = index,
                                size = tileSize(state.word).dp,
                                fontSize = tileFontSize(state.word).sp,
                                onTileClick = { click -> viewModel.onTileClick(click) }
                            )
                        }
                    }
                    is MichaelState.Idle -> EmptyGame(
                        "MICHAEL",
                        tileSize(initalWord).dp,
                        tileFontSize(initalWord).sp,
                    )
                }
            }
            when (val state = viewModel.gameState) {
                is MichaelState.Idle -> StartRow { viewModel.onStartClick() }
                is MichaelState.Playing -> {
                    Keyboard(
                        keyboardTileWidth = keyboardTileWidth(),
                        keyboardTileHeight =keyboardTileHeight(),
                        enterEnabled = state.tileRows[state.activeRow].tiles.last().character != null,
                        onTileClick = { clickedLetter -> viewModel.onKeyboardClick(clickedLetter) },
                        onDeleteClick = { viewModel.onDeleteClick() },
                        onEnterClick = { viewModel.onEnterClick() }
                    )
                }
            }
        }
        when (val state = viewModel.gameState) {
            is MichaelState.Playing -> if (state.showSnackbar) scope.launch {
                snackbarHostState.showSnackbar("not a word")
            }
            is MichaelState.Error -> scope.launch { snackbarHostState.showSnackbar("api error try again") }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun EmptyGame(
    word: String,
    tileSize: Dp,
    fontSize: TextUnit,
) {
    Column {
        word.mapIndexed { index, _ ->
            LetterRow(
                word.map {
                    Tile(TileState.IDLE, it)
                },
                index,
                tileSize,
                fontSize
            ) {}
        }
    }
}
