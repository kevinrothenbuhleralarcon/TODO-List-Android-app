package ch.kra.todo.todo.data.remote

import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.TodoListDTO
import retrofit2.http.GET
import retrofit2.http.Header

interface TodoApi {

    @GET("api/todoList")
    suspend fun getTodoList(
        @Header("x-access-token") token: String
    ): TodoListDTO
}