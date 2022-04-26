package ch.kra.todo.auth.domain.model

data class User (
    val username: String? = null,
    val email: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null
)