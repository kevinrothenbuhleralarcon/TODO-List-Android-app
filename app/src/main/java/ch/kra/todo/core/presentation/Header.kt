package ch.kra.todo.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Header(
    modifier: Modifier = Modifier,
    username: String = ""
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.primary)
    ) {
        if (username.isNotEmpty()) {
            Text(
                text = username,
                textAlign = TextAlign.Right
            )
        }
    }
}