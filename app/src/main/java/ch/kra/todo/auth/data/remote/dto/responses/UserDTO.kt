package ch.kra.todo.auth.data.remote.dto.responses

import ch.kra.todo.auth.domain.model.User

data class UserDTO(
    val email: String,
    val username: String
) {
    fun toUser(): User {
        return User(
            username = username,
            email = email
        )
    }
}