package ch.kra.todo.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.kra.todo.core.presentation.ui.theme.BorderColor

@Composable
fun Footer(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(BorderColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Author: Kevin Rothenbühler-Alarcon")
    }
}