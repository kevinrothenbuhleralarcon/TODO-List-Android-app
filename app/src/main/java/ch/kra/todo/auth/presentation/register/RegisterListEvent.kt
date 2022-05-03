package ch.kra.todo.auth.presentation.register

sealed class RegisterListEvent {
    data class EnteredUsername(val value: String): RegisterListEvent()
    data class EnteredEmail(val value: String): RegisterListEvent()
    data class EnteredPassword1(val value: String): RegisterListEvent()
    data class EnteredPassword2(val value: String): RegisterListEvent()
    object TogglePasswordVisibility: RegisterListEvent()
    object Register: RegisterListEvent()
    object NavigateBack: RegisterListEvent()
}