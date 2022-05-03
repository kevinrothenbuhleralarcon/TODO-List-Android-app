package ch.kra.todo.auth.data.remote

import ch.kra.todo.auth.data.remote.dto.requests.LoginRequestDTO
import ch.kra.todo.auth.data.remote.dto.requests.RegisterRequestDTO
import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/login")
    suspend fun login(
        @Body login: LoginRequestDTO
    ) : LoginResponseDTO

    @POST("api/register")
    suspend fun register(
        @Body register: RegisterRequestDTO
    ) : LoginResponseDTO
}
