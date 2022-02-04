package com.mikesmith.michael.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NewWord(
    newWordText: String,
    onClick: (String) -> Unit = { },
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp),
    ) {
        Text(
            text = newWordText,
            color = Color.White
        )
        OutlinedButton(
            onClick = { onClick(newWordText) }
        ) {
            Text(
                text = "set the next word!"
            )
        }
    }
}

@Preview
@Composable
fun PreviewNewWord() {
    NewWord(
        "PANCAKES"
    )
}