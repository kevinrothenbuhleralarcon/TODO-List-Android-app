package ch.kra.todo.auth.data.remote.dto.requests

data class LoginRequestDTO (
    val username: String,
    val password: String,
)