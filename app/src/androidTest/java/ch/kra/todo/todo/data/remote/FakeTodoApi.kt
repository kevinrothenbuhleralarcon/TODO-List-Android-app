package ch.kra.todo.todo.data.remote

import ch.kra.todo.auth.data.remote.dto.responses.LoginResponseDTO
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.data.remote.dto.response.GetTodoListResponseDTO
import ch.kra.todo.todo.data.remote.dto.response.GetTodoResponseDTO
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeTodoApi : TodoApi {

    var todoList = mutableListOf<TodoDTO>()

    override suspend fun getTodoList(token: String): GetTodoListResponseDTO {
        if (token == "success") return GetTodoListResponseDTO(todoList)
        throw (HttpException(
            Response.error<LoginResponseDTO>(
                401,
                ResponseBody.create(MediaType.parse("plain/text"), "Invalid token")
            )
        ))
    }

    override suspend fun getTodo(token: String, id: Int): GetTodoResponseDTO {
        if (token == "success") return GetTodoResponseDTO(todoList.find { it.id == id })
        throw (HttpException(
            Response.error<LoginResponseDTO>(
                401,
                ResponseBody.create(MediaType.parse("plain/text"), "Invalid token")
            )
        ))
    }

    override suspend fun addTodo(token: String, todo: AddEditTodoRequestDTO): Response<String> {
        if (token == "success") {
            todoList.add(todo.todo.copy(id = todoList.size + 1))
            return Response.success("New todo added")
        }
        return Response.error(
            401,
            ResponseBody.create(MediaType.parse("plain/text"), "Invalid token")
        )
    }

    override suspend fun updateTodo(token: String, todo: AddEditTodoRequestDTO): Response<String> {
        if (token == "success") {
            val previousTodo = todoList.find { it.id == todo.todo.id }
            val index = todoList.indexOf(previousTodo)
            return if (index != -1) {
                todoList[index] = todo.todo
                Response.success("Todo updated")
            } else {
                Response.error(
                    400,
                    ResponseBody.create(MediaType.parse("plain/text"), "Update failed")
                )
            }
        }
        return Response.error(
            401,
            ResponseBody.create(MediaType.parse("plain/text"), "Invalid token")
        )
    }

    override suspend fun deleteTodo(token: String, id: Int): Response<String> {
        if (token == "success") {
            return if (todoList.remove(todoList.find { it.id == id })) {
                Response.success("Todo deleted")
            } else {
                Response.error(
                    400,
                    ResponseBody.create(
                        MediaType.parse("plain/text"),
                        "No todo with this id to delete for the connected user"
                    )
                )
            }
        }
        return Response.error(
            401,
            ResponseBody.create(MediaType.parse("plain/text"), "Invalid token")
        )
    }
}