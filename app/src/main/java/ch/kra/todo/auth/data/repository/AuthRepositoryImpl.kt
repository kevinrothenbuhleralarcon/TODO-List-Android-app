package ch.kra.todo.auth.data.repository

import android.util.Log
import ch.kra.todo.auth.data.remote.AuthApi
import ch.kra.todo.auth.data.remote.dto.requests.LoginRequestDTO
import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override fun login(username: String, password: String): Flow<Resource<LoginResponseDTO>> = flow {
        emit(Resource.Loading())
        try {
            val loginRequest = LoginRequestDTO(username, password)
            val loginResponse = authApi.login(loginRequest)
            if(loginResponse.isSuccessful) {

            }
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = e.message()
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }
}