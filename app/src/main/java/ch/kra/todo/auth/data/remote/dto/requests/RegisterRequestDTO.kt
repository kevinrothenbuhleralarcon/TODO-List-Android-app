package ch.kra.todo.auth.data.remote.dto.requests

data class RegisterRequestDTO(
    val username: String,
    val email: String,
    val password: String
)