package ch.kra.todo.auth.presentation.login

import androidx.compose.ui.focus.FocusState

sealed class AuthListEvent {
    data class EnteredUsername(val value: String): AuthListEvent()
    data class EnteredPassword(val value: String): AuthListEvent()
    object TogglePasswordVisibility: AuthListEvent()
    object Login: AuthListEvent()
    data class OnNavigateToWebClient(val url: String): AuthListEvent()
}
