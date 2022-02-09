package com.mikesmith.michael

import com.mikesmith.michael.components.KeyboardKeyData

sealed class MichaelState {

    object Idle : MichaelState()

    data class Loading(
        val word: String,
    ) : MichaelState()

    data class Error(
        val word: String,
    ) : MichaelState()

    data class Won(
        val word: String,
        val tileRows: List<TileRow>,
        val keyboard: List<List<KeyboardKeyData>>,
        val newWord: String = ""
    ) : MichaelState()

    data class Lost(
        val word: String,
        val tileRows: List<TileRow>,
    ) : MichaelState()

    data class Playing(
        val word: String,
        val activeRow: Int,
        val tileRows: List<TileRow>,
        val keyboard: List<List<KeyboardKeyData>>,
        val showSnackbar: Boolean = false,
    ) : MichaelState()
}