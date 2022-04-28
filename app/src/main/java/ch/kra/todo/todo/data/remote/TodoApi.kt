package ch.kra.todo.todo.data.remote

import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.GetTodoListResponseDTO
import ch.kra.todo.todo.data.remote.dto.GetTodoResponseDTO
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoApi {

    @GET("api/todoList")
    suspend fun getTodoList(
        @Header("x-access-token") token: String
    ): GetTodoListResponseDTO

    @GET("api/todo")
    suspend fun getTodo(
        @Header("x-access-token") token: String,
        @Query("id") id: Int
    ): GetTodoResponseDTO
}