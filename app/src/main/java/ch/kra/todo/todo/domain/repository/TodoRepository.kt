package ch.kra.todo.todo.domain.repository

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodoList(token: String): Flow<Resource<List<Todo>>>

    fun getTodo(token: String, todoId: Int): Flow<Resource<Todo>>

    fun addTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>>

    fun updateTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>>

    fun deleteTodo(token: String, todoId: Int): Flow<Resource<String>>
}