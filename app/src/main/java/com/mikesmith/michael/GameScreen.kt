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
import com.mikesmith.michael.components.*
import kotlinx.coroutines.launch

private val initialWord = "MICHAEL"

@Composable
fun GameScreen(viewModel: MichaelViewModel = viewModel(), screenWidth: Dp) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val tileScale = 50
    fun tileSize(word: String) = ((screenWidth.value - tileScale) / word.length) - 4
    fun tileFontSize(word: String) = 200 / word.length
    fun keyboardTileWidth() = ((screenWidth.value - 20f) / 10) - 6
    fun keyboardTileHeight() = ((screenWidth.value - 20f) / 6) - 6

    Box(contentAlignment = Alignment.TopCenter) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            TopBar(viewModel)
            Row {
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(tileScale.dp / 2, 8.dp)
            ) {

                when (val state = viewModel.gameState) {
                    is MichaelState.Loading -> Box {
                        Text(text = "loading")
                    }
                    is MichaelState.Won -> Column(
                        Modifier
                            .width(screenWidth - 12.dp)
                            .height(screenWidth + 12.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
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
                    is MichaelState.Lost -> Column(
                        Modifier
                            .width(screenWidth - 12.dp)
                            .height(screenWidth + 12.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
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
                    is MichaelState.Playing -> Column(
                        Modifier
                            .height(screenWidth - tileScale.dp + (tileSize(state.word).dp + 4.dp)),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
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
                        tileSize(initialWord).dp,
                        tileFontSize(initialWord).sp,
                    )
                }
            }
            when (val state = viewModel.gameState) {
                is MichaelState.Idle -> StartRow { viewModel.onStartClick() }
                is MichaelState.Playing -> {
                    Keyboard(
                        keyboardTileWidth = keyboardTileWidth(),
                        keyboardTileHeight = keyboardTileHeight(),
                        enterEnabled = state.tileRows[state.activeRow].tiles.last().character != null,
                        onTileClick = { clickedLetter -> viewModel.onKeyboardClick(clickedLetter) },
                        onDeleteClick = { viewModel.onDeleteClick() },
                        onEnterClick = { viewModel.onEnterClick() }
                    )
                }
                is MichaelState.Won -> {
                    NewWord(newWordText = state.newWord) { viewModel.onNewWordClick(it) }
                    Keyboard(
                        keyboardTileWidth = keyboardTileWidth(),
                        keyboardTileHeight = keyboardTileHeight(),
                        enterEnabled = true,
                        onTileClick = { viewModel.onKeyboardClick(it) },
                        onDeleteClick = { viewModel.onDeleteNewWord() },
                        onEnterClick = { }
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
