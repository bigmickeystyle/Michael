package com.mikesmith.michael

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
    ) : MichaelState()

    data class Playing(
        val word: String,
        val activeRow: Int,
        val tileRows: List<TileRow>,
        val showSnackbar: Boolean = false,
    ) : MichaelState()
}