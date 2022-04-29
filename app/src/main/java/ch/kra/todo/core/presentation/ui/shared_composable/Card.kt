package ch.kra.todo.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import ch.kra.todo.core.presentation.ui.theme.BorderColor

@Composable
fun TodoCard(
    modifier: Modifier = Modifier,
    composable: @Composable() () -> Unit
) {
    Box(
        modifier = modifier
            .padding(
                start = 20.dp,
                top = 0.dp,
                end = 20.dp,
                bottom = 20.dp
            )
            .background(MaterialTheme.colors.surface)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
            .padding(20.dp)
    ) {
        composable()
    }
}