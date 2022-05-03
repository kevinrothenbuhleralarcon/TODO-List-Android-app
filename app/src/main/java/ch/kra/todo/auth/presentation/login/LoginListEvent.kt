package ch.kra.todo.auth.presentation.login

sealed class LoginListEvent {
    data class EnteredUsername(val value: String): LoginListEvent()
    data class EnteredPassword(val value: String): LoginListEvent()
    object TogglePasswordVisibility: LoginListEvent()
    object Login: LoginListEvent()
    object Register: LoginListEvent()
}
