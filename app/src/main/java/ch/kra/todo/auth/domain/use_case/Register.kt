package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.Flow

class Register(
    private val authRepository: AuthRepository
) {
    operator fun invoke(username: String, email: String, password: String): Flow<Resource<LoginResponseDTO>> {
        return authRepository.register(username, email, password)
    }
}