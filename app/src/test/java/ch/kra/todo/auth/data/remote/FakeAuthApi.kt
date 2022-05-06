package ch.kra.todo.auth.data.remote

import ch.kra.todo.auth.data.remote.dto.requests.LoginRequestDTO
import ch.kra.todo.auth.data.remote.dto.requests.RegisterRequestDTO
import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeAuthApi: AuthApi {
    override suspend fun login(login: LoginRequestDTO): LoginResponseDTO {
        if (login.username == "success") {
            return LoginResponseDTO(
                token = "success",
                token_lifetime = "12h",
                username = login.username
            )
        }
        throw (HttpException(Response.error<LoginResponseDTO>(400, ResponseBody.create(MediaType.parse("plain/text"),"Invalid Credential"))))
    }

    override suspend fun register(register: RegisterRequestDTO): LoginResponseDTO {
        if (register.username == "success") {
            return LoginResponseDTO(
                token = "success",
                token_lifetime = "12h",
                username = register.username
            )
        }
        if (register.username == "existing") {
            throw (HttpException(Response.error<LoginResponseDTO>(400, ResponseBody.create(MediaType.parse("plain/text"),"Username already in use. Please login"))))
        }
        throw (HttpException(Response.error<LoginResponseDTO>(400, ResponseBody.create(MediaType.parse("plain/text"),"Invalid Credential"))))
    }
}