package com.mikesmith.michael

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed class MichaelState(val word: String) {

    object Start : MichaelState("michael")

    data class Playing(val tiles: List<Tile>) : MichaelState("start")
}

data class Tile(val character: Char, val tileState: TileState)

enum class TileState {
    GUESSING, EXACT_MATCH, PARTIAL_MATCH, NO_MATCH
}

class MichaelViewModel : ViewModel() {

    var gameState by mutableStateOf<MichaelState>(MichaelState.Start)

    fun onTileClick(text: String) {
        gameState = when (gameState) {
            is MichaelState.Playing -> MichaelState.Start
            MichaelState.Start -> MichaelState.Playing(listOf())
        }
    }
}