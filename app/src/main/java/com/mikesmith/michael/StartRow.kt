package com.mikesmith.michael;

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StartRow(onClick: () -> Unit = { }) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedButton(
            onClick = { onClick() }
        ) {
            Text(
                text = "game start"
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewStartRow() {
    StartRow()
}
