package ch.kra.todo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingWrapper(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    composable: @Composable() () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        } else {
            composable()
        }
    }
}