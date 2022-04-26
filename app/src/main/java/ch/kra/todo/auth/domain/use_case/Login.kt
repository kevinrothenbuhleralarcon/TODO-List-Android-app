package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.Flow

class Login (
    private val repository: AuthRepository
) {
    operator fun invoke(username: String, password: String): Flow<Resource<LoginResponseDTO>> {
        return repository.login(username, password)
    }
}