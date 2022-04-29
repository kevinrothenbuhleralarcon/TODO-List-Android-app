package ch.kra.todo.todo.data.remote

import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.data.remote.dto.response.GetTodoListResponseDTO
import ch.kra.todo.todo.data.remote.dto.response.GetTodoResponseDTO
import retrofit2.Response
import retrofit2.http.*

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

    @POST("api/add")
    suspend fun addTodo(
        @Header("x-access-token") token: String,
        @Body todo: AddEditTodoRequestDTO
    ): Response<String>

    @PUT("api/update")
    suspend fun updateTodo(
        @Header("x-access-token") token: String,
        @Body todo: AddEditTodoRequestDTO
    ): Response<String>

    @DELETE("api/delete")
    suspend fun deleteTodo(
        @Header("x-access-token") token: String,
        @Query("id") id: Int
    ): Response<String>
}