package ch.kra.todo.auth.data.remote.dto.responses

data class LoginResponseDTO(
    val token: String,
    val token_lifetime: String,
    val username: String
)