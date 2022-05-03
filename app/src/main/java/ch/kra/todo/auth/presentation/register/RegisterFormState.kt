package ch.kra.todo.auth.presentation.register

import ch.kra.todo.core.UIText

data class RegisterFormState(
    val username: String = "",
    val usernameError: UIText? = null,
    val email: String = "",
    val emailError: UIText? = null,
    val password1: String = "",
    val password1Error: UIText? = null,
    val password2: String = "",
    val password2Error: UIText? = null,
    val passwordVisibility: Boolean = false,
    val isLoading: Boolean = false
)
