package com.mikesmith.michael

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikesmith.michael.MichaelState.Playing
import com.mikesmith.michael.MichaelState.Won
import com.mikesmith.michael.components.*
import com.mikesmith.michael.network.MichaelService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val michaelService: MichaelService,
) : ViewModel() {

    var gameState by mutableStateOf<MichaelState>(MichaelState.Idle)

    private var currentWord: String = "ERROR"

    init {
        setWord()
    }

    private fun setWord() {
        viewModelScope.launch {
            michaelService.getWordForToday().body()?.wordForToday?.let { word ->
                currentWord = word.uppercase()
            }
        }
    }

    fun onStartClick() {
        gameState = Playing(
            word = currentWord,
            activeRow = 0,
            tileRows = (0..currentWord.length).map {
                TileRow(
                    currentWord.map {
                        Tile(TileState.IDLE)
                    }
                )
            },
            keyboard = keyList
        )
    }

    fun onTileClick(clickData: MichaelClickData) {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is Playing) {
                    if (clickData.tryPosition == activeRow) {
                        gameState = Playing(
                            word,
                            activeRow,
                            newTilesFromTileClick(clickData),
                            keyboard
                        )
                    }
                }
            }
        }
    }

    fun onKeyboardClick(clickedLetter: Char) {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                when (this) {
                    is Playing -> gameState = Playing(
                        word = word,
                        activeRow = activeRow,
                        tileRows = newTileStateFromKeyboardClick(clickedLetter),
                        keyboard
                    )
                    is Won -> gameState = Won(
                        word = word,
                        tileRows = tileRows,
                        newWord = newWord + clickedLetter,
                        keyboard = keyList
                    )
                }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is Playing) {
                    gameState = Playing(
                        word,
                        activeRow,
                        newTileStateFromDeleteClick(),
                        keyboard
                    )
                }
            }
        }
    }

    fun onEnterClick(dictionaryEntries: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            with(gameState) {
                if (this is Playing) {
                    val tiles = this.tileRows[activeRow].tiles
                    val guess = tiles.map { it.character }.joinToString("")
                        .uppercase()

                    gameState = if (guess == word.uppercase()) {
                        toWonState()
                    } else {
                        when (dictionaryEntries.find { it == guess }) {
                            null -> Playing(
                                word,
                                activeRow,
                                tileRows,
                                keyboard,
                                true
                            )
                            else -> checkWrongGuess()
                        }
                    }
                }
            }
        }
    }

    private fun Playing.toWonState(): MichaelState {
        return Won(
            word,
            tileRows.foldIndexed(emptyList()) { index, acc, tileRow ->
                if (index == activeRow) {
                    acc + tileRow.copy(
                        tiles = tileRow.tiles.map { tile ->
                            tile.copy(tileState = TileState.RIGHT)
                        }
                    )
                } else {
                    acc + tileRow
                }
            },
            keyList
        )
    }

    private fun Playing.checkWrongGuess(): MichaelState {
        val newTiles = tileRows.foldIndexed(emptyList<TileRow>()) { index, acc, tileRow ->
            if (index == activeRow) {
                acc + tileRow.copy(
                    tiles = tileRow.tiles.correctTiles(word)
                )
            } else {
                acc + tileRow
            }
        }
        val guess = newTiles[activeRow].tiles
        val guessWord = guess.map { it.character }
        val newKeyboard = keyboard.map { row ->
            row.map { key ->
                if (guessWord.contains(key.letter)) {
                    when (val tile = guess.find { it.character == key.letter }?.tileState) {
                        null -> key
                        TileState.IDLE -> key
                        TileState.GUESSING -> key
                        TileState.RIGHT -> key.copy(state = KeyboardKeyState.GREEN)
                        TileState.GOOD_BUT_NOT_RIGHT -> key.copy(state = KeyboardKeyState.YELLOW)
                        TileState.NO_MATCH -> key.copy(state = KeyboardKeyState.CROSSED)
                    }
                } else {
                    key
                }
            }
        }


        return if (activeRow + 1 == word.length + 1) {
            MichaelState.Lost(
                word,
                newTiles
            )
        } else {
            Playing(
                word,
                activeRow + 1,
                newTiles,
                newKeyboard
            )
        }
    }

    private fun List<Tile>.correctTiles(correctWord: String): List<Tile> {
        val wordMap = correctWord
            .mapIndexed { index, char -> char to index }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second } }

        val matchSweep = mapIndexed { index, tile ->
            wordMap[tile.character]?.let {
                if (it.contains(index)) {
                    tile.copy(tileState = TileState.RIGHT)
                } else {
                    tile
                }
            } ?: tile
        }
        return matchSweep.mapIndexed { index, tile ->
            when {
                tile.tileState == TileState.RIGHT -> tile
                wordMap[tile.character] != null -> {
                    val exactMatchCount =
                        matchSweep.count { it.character == tile.character && it.tileState == TileState.RIGHT }
                    val partialMatchCount = wordMap[tile.character]!!.size - exactMatchCount
                    val correctLettersBeforeIndex =
                        take(index).count { it.character == tile.character }
                    if (correctLettersBeforeIndex < partialMatchCount) {
                        tile.copy(tileState = TileState.GOOD_BUT_NOT_RIGHT)
                    } else {
                        tile.copy(tileState = TileState.NO_MATCH)
                    }
                }
                else -> tile.copy(tileState = TileState.NO_MATCH)
            }
        }
    }

    private fun Playing.newTileStateFromDeleteClick(): List<TileRow> {
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

    private fun Playing.newTileStateFromKeyboardClick(clickedLetter: Char): List<TileRow> {
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

    private fun Playing.newTilesFromTileClick(clickData: MichaelClickData): List<TileRow> {
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

    fun onNewWordClick(newWord: String) {
        if (newWord.length > 3) {
            viewModelScope.launch {
                val response = michaelService.setWordForToday(mapOf("wordForToday" to newWord))
                if (response.isSuccessful) {
                    setWord()
                    gameState = Playing(
                        newWord,
                        0,
                        (0..newWord.length).map {
                            TileRow(
                                newWord.map {
                                    Tile(TileState.IDLE)
                                }
                            )
                        },
                        keyList
                    )
                }
            }
        }
    }

    fun onDeleteNewWord() {
        with(gameState) {
            if (this is Won) {
                gameState = this.copy(
                    newWord = newWord.dropLast(1)
                )
            }
        }
    }
}

data class MichaelClickData(
    val tryPosition: Int,
    val wordPosition: Int,
    val currentState: TileState,
)
