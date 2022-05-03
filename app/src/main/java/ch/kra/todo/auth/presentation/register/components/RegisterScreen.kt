package ch.kra.todo.auth.presentation.register.components

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.auth.presentation.register.RegisterViewModel
import ch.kra.todo.core.UIEvent

@Composable
fun RegisterScreen(
     viewModel: RegisterViewModel = hiltViewModel(),
     navigate: (UIEvent.Navigate) -> Unit,
     popBackStack: () -> Unit
) {

}