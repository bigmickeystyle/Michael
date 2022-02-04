package com.mikesmith.michael.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun NavButton() {
    val clicked = remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            clicked.value = clicked.value.not()
        }
    ) {
        if (clicked.value) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "test"
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "test"
            )
        }
    }
}
