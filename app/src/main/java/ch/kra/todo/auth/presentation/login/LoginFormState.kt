package ch.kra.todo.auth.presentation.login

import ch.kra.todo.core.UIText

data class LoginFormState(
    val username: String = "",
    val usernameError: UIText? = null,
    val password: String = "",
    val passwordError: UIText? = null,
    val passwordVisibility: Boolean = false
)
