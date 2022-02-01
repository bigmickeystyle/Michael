package com.mikesmith.michael

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikesmith.michael.network.MichaelService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val michaelService: MichaelService,
) : ViewModel() {

    var gameState by mutableStateOf<MichaelState>(MichaelState.Idle)

    private var currentWord: String = "ERROR"

    init {
        setWord()
    }

    private val lineList =
        context.assets.open("words.txt").bufferedReader().use { it.readText() }.split("\n")

    fun setWord() {
        viewModelScope.launch {
            michaelService.getWordForToday().body()?.wordForToday?.let { word ->
                currentWord = word.uppercase()
            }
        }
    }

    fun onStartClick() {
        gameState = MichaelState.Playing(
            currentWord,
            0,
            currentWord.map {
                TileRow(
                    currentWord.map {
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
                    val guess = this.tileRows[activeRow].tiles.map { it.character }.joinToString("")
                        .uppercase()
                    gameState = if (guess == word.uppercase()) {
                        MichaelState.Won(word)
                    } else {
                        try {
                            when (lineList.find { it.uppercase() == guess }) {
                                null -> MichaelState.Playing(
                                    word,
                                    activeRow,
                                    tileRows,
                                    true
                                )
                                else -> checkWrongGuess()
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
        val cachedWord = word.toMutableList()
        return MichaelState.Playing(
            word,
            activeRow + 1,
            tileRows.foldIndexed(emptyList()) { index, acc, tileRow ->
                if (index == activeRow) {
                    acc + tileRow.copy(
                        tiles = tileRow.tiles.mapIndexed { wordIndex, tile ->
                            when {
                                tile.character == word[wordIndex] -> {
                                    tile.copy(tileState = TileState.RIGHT).also {
                                        cachedWord.remove(tile.character)
                                    }
                                }
                                cachedWord.contains(tile.character) -> {
                                    tile.copy(tileState = TileState.GOOD_BUT_NOT_RIGHT).also {
                                        cachedWord.remove(tile.character)
                                    }
                                }
                                else -> tile.copy(tileState = TileState.NO_MATCH)
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
        gameState = MichaelState.Idle.also {
            setWord()
        }
    }
}

data class MichaelClickData(
    val tryPosition: Int,
    val wordPosition: Int,
    val currentState: TileState,
)
