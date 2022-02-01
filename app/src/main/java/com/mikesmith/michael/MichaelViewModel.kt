package com.mikesmith.michael

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikesmith.michael.network.DictionaryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class TileRow(val tiles: List<Tile>)
data class Tile(val tileState: TileState, val character: Char? = null)

enum class TileState {
    IDLE, GUESSING, RIGHT, GOOD_BUT_NOT_RIGHT, NO_MATCH
}

@HiltViewModel
class MichaelViewModel
@Inject
constructor(
    private val dictionaryService: DictionaryService,
) : ViewModel() {

    var gameState by mutableStateOf<MichaelState>(MichaelState.Idle)

    fun onStartClick(word: String) {
        gameState = MichaelState.Playing(
            word,
            0,
            word.map {
                TileRow(
                    word.map {
                        Tile(TileState.IDLE)
                    }
                )
            }
        )
    }

    fun onTileClick(clickData: MichaelClickData) {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is MichaelState.Playing) {
                    if (clickData.tryPosition == activeRow) {
                        gameState = MichaelState.Playing(
                            word,
                            activeRow,
                            newTilesFromTileClick(clickData)
                        )
                    }
                }
            }
        }
    }

    fun onKeyboardClick(clickedLetter: Char) {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is MichaelState.Playing) {
                    gameState = MichaelState.Playing(
                        word,
                        activeRow,
                        newTileStateFromKeyboardClick(clickedLetter)
                    )
                }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is MichaelState.Playing) {
                    gameState = MichaelState.Playing(
                        word,
                        activeRow,
                        newTileStateFromDeleteClick()
                    )
                }
            }
        }
    }

    fun onEnterClick() {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is MichaelState.Playing) {
                    gameState = MichaelState.Loading(word)

                    val guess = this.tileRows[activeRow].tiles.map { it.character }.joinToString("")
                    gameState = if (guess.uppercase() == word.uppercase()) {
                        MichaelState.Won(word)
                    } else {
                        try {
                            val dictionaryResponse = dictionaryService.checkValidity(guess)

                            when (dictionaryResponse.code()) {
                                200 -> checkWrongGuess()
                                else -> MichaelState.Playing(
                                    word,
                                    activeRow,
                                    tileRows,
                                    true
                                )
                            }
                        } catch (e: IOException) {
                            println(e.message)
                            MichaelState.Error(word)
                        }
                    }
                }
            }
        }
    }

    private fun MichaelState.Playing.checkWrongGuess(): MichaelState.Playing {
        return MichaelState.Playing(
            word,
            activeRow + 1,
            tileRows.foldIndexed(emptyList()) { index, acc, tileRow ->
                if (index == activeRow) {
                    acc + tileRow.copy(
                        tiles = tileRow.tiles.mapIndexed { wordIndex, tile ->
                            when {
                                tile.character == word[wordIndex] -> tile.copy(tileState = TileState.RIGHT)
                                tile.character == null -> tile
                                word.contains(tile.character) -> tile.copy(tileState = TileState.GOOD_BUT_NOT_RIGHT)
                                else -> tile
                            }
                        }
                    )
                } else {
                    acc + tileRow
                }
            }
        )
    }

    private fun MichaelState.Playing.newTileStateFromDeleteClick(): List<TileRow> {
        return tileRows.foldIndexed(emptyList()) { rowIndex, acc, element ->
            if (this.activeRow == rowIndex) {
                acc + element.copy(
                    tiles = element.tiles.mapIndexed { index, tile ->
                        when {
                            tile.character != null && index == element.tiles.size - 1 -> tile.copy(
                                character = null)
                            tile.character != null && element.tiles[index + 1].character == null -> tile.copy(
                                character = null)
                            else -> tile
                        }
                    }
                )
            } else {
                acc + element
            }
        }
    }

    private fun MichaelState.Playing.newTileStateFromKeyboardClick(clickedLetter: Char): List<TileRow> {
        return tileRows.foldIndexed(emptyList()) { rowIndex, acc, element ->
            if (this.activeRow == rowIndex) {
                acc + element.copy(
                    tiles = element.tiles.mapIndexed { index, tile ->
                        when {
                            tile.character == null && index == 0 -> tile.copy(character = clickedLetter)
                            tile.character == null && element.tiles[index - 1].character != null -> tile.copy(
                                character = clickedLetter)
                            else -> tile
                        }
                    }
                )
            } else {
                acc + element
            }
        }
    }

    private fun MichaelState.Playing.newTilesFromTileClick(clickData: MichaelClickData): List<TileRow> {
        return tileRows.foldIndexed(emptyList()) { rowIndex, acc, element ->
            if (this.activeRow == rowIndex) {
                acc + element.copy(
                    tiles = element.tiles.mapIndexed { index, tile ->
                        when {
                            index == clickData.wordPosition -> tile.copy(tileState = clickData.newState())
                            tile.tileState == TileState.GUESSING -> tile.copy(tileState = TileState.IDLE)
                            else -> tile
                        }
                    }
                )
            } else {
                acc + element
            }
        }
    }

    private fun MichaelClickData.newState() =
        when (currentState) {
            TileState.IDLE -> TileState.GUESSING
            TileState.GUESSING -> TileState.IDLE
            TileState.RIGHT -> TileState.RIGHT
            TileState.GOOD_BUT_NOT_RIGHT -> TileState.GOOD_BUT_NOT_RIGHT
            TileState.NO_MATCH -> TileState.NO_MATCH
        }

    fun onRestartClick() {
        gameState = MichaelState.Idle
    }
}

data class MichaelClickData(
    val tryPosition: Int,
    val wordPosition: Int,
    val currentState: TileState,
)
