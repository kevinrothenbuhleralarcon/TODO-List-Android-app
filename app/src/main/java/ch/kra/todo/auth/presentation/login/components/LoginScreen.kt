package ch.kra.todo.auth.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = "Hello world")
    }
}