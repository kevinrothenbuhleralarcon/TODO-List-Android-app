package ch.kra.todo.auth.domain.repository

import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(username: String, password: String): Flow<Resource<LoginResponseDTO>>
    fun register(username: String, email: String, password: String): Flow<Resource<LoginResponseDTO>>
}