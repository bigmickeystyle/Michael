package com.mikesmith.michael.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mikesmith.michael.MichaelViewModel

@Composable
fun TopBar(viewModel: MichaelViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        NavButton()
        Box(modifier = Modifier.clickable {
            viewModel.onRestartClick()
        }) {
            Text(
                text = "Michael",
                fontSize = 45.sp
            )
        }
        NavButton()
    }
}