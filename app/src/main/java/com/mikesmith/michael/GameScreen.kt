package com.mikesmith.michael

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

@Composable
fun GameScreen(viewModel: MichaelViewModel = viewModel(), screenWidth: Dp) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun tileSize() = ((screenWidth.value - 40f) / viewModel.gameState.word.length) - 12
    fun tileFontSize() = 200 / viewModel.gameState.word.length
    fun keyboardTileSize() = ((screenWidth.value - 20f) / 10) - 6

    Box(contentAlignment = Alignment.BottomCenter) {
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
                val fontSize = tileFontSize()

                when (val state = viewModel.gameState) {
                    is MichaelState.Loading -> Box {
                        Text(text = "loading")
                    }
                    is MichaelState.Playing -> Column {
                        state.tileRows.mapIndexed { index, tileRow ->
                            LetterRow(
                                tiles = tileRow.tiles,
                                tryPosition = index,
                                size = currentTileSize.dp,
                                fontSize = fontSize.sp,
                                onTileClick = { click -> viewModel.onTileClick(click) }
                            )
                        }
                    }
                    MichaelState.Idle -> EmptyGame(
                        viewModel.gameState.word,
                        currentTileSize.dp,
                        fontSize.sp,
                    )
                }
            }
            when (val state = viewModel.gameState) {
                MichaelState.Idle -> StartRow { viewModel.onStartClick("start") }
                is MichaelState.Playing -> {
                    Keyboard(
                        keyboardTileWidth = keyboardTileSize(),
                        keyboardTileHeight = tileSize(),
                        enterEnabled = state.tileRows[state.activeRow].tiles.last().character != null,
                        onTileClick = { clickedLetter -> viewModel.onKeyboardClick(clickedLetter) },
                        onDeleteClick = { viewModel.onDeleteClick() },
                        onEnterClick = { viewModel.onEnterClick() }
                    )
                }
            }
        }
        when (val state = viewModel.gameState) {
            is MichaelState.Playing -> if (state.showSnackbar) scope.launch { snackbarHostState.showSnackbar("not a word") }
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
