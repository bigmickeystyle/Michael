package com.mikesmith.michael

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed class MichaelState {

    object Start : MichaelState()

    data class Playing(val tiles: List<Tile>) : MichaelState()
}

data class Tile(val character: String, val tileState: TileState)

enum class TileState {
    GUESSING, EXACT_MATCH, PARTIAL_MATCH, NO_MATCH
}

class MichaelViewModel : ViewModel() {
    var gameState: MichaelState by mutableStateOf(MichaelState.Start)
}