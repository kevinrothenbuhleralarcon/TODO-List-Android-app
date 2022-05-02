package ch.kra.todo.auth.presentation.login

data class LoginFormState(
    val username: String = "",
    val usernameError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisibility: Boolean = false
)
