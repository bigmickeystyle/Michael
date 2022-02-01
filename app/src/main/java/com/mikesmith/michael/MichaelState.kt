package com.mikesmith.michael

sealed class MichaelState(open val word: String) {

    object Idle : MichaelState("MICHAEL")

    data class Loading(
        override val word: String,
    ) : MichaelState(word)

    data class Error(
        override val word: String,
    ) : MichaelState(word)

    data class Won(
        override val word: String,
    ) : MichaelState(word)

    data class Playing(
        override val word: String,
        val activeRow: Int,
        val tileRows: List<TileRow>,
        val showSnackbar: Boolean = false,
    ) : MichaelState(word)
}