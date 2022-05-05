package ch.kra.todo.auth.data.repository

import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthRepository: AuthRepository {

    override fun login(username: String, password: String): Flow<Resource<LoginResponseDTO>> {
        return flow {
            emit(Resource.Loading())
            if(username == "success" && password == "success") {
                emit(Resource.Success(data = LoginResponseDTO(token = "success", token_lifetime = "12h", username = "success")))
            } else {
                emit(Resource.Error(message = "Invalid username"))
            }
        }
    }

    override fun register(
        username: String,
        email: String,
        password: String
    ): Flow<Resource<LoginResponseDTO>> {
        return flow {
            emit(Resource.Loading())
            if(username == "success" && email == "success@test.com" && password == "success") {
                emit(Resource.Success(data = LoginResponseDTO(token = "success", token_lifetime = "12h", username = "success")))
            } else {
                emit(Resource.Error(message = "Invalid username"))
            }
        }
    }
}