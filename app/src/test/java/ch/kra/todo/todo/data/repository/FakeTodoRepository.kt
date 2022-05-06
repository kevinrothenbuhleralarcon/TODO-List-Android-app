package ch.kra.todo.todo.data.repository

import android.util.Log
import ch.kra.todo.core.Constants.INVALID_TOKEN
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTodoRepository: TodoRepository {

    private val todos = mutableListOf<Todo>()

    override fun getTodoList(token: String): Flow<Resource<List<Todo>>> {
        return flow {
            emit(Resource.Loading())
            if (token == "success") {
                emit(Resource.Success(data = todos))
            } else {
                emit(Resource.Error(message = INVALID_TOKEN))
            }
        }
    }

    override fun getTodo(token: String, todoId: Int): Flow<Resource<Todo>> {
        return flow {
            emit(Resource.Loading())
            if (token == "success") {
                todos.find { it.id == todoId }?.let {
                    emit(Resource.Success(data = it))
                } ?: emit(Resource.Error(message = "No todo with this id"))
            } else {
                emit(Resource.Error(message = INVALID_TOKEN))
            }
        }
    }

    override fun addTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            if (token == "success") {
                println("Added: ${todos.add(request.todo.toTodo())}")
                emit(Resource.Success(data = "Todo added"))
            } else {
                emit(Resource.Error(message = INVALID_TOKEN))
            }
        }
    }

    override fun updateTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            if (token == "success") {
                todos.find { it.id == request.todo.id }?.let {
                    todos[todos.indexOf(it)] = request.todo.toTodo()
                    emit(Resource.Success(data = "Todo updated"))
                } ?: emit(Resource.Error(message = "No todo with this id"))
            } else {
                emit(Resource.Error(message = INVALID_TOKEN))
            }
        }
    }

    override fun deleteTodo(token: String, todoId: Int): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            if (token == "success") {
                todos.find { it.id == todoId }?.let {
                    if (todos.remove(it)) {
                        emit(Resource.Success(data = "Todo deleted"))
                    } else {
                        emit(Resource.Error(message = "Could not delete the todo"))
                    }
                } ?: emit(Resource.Error(message = "No todo with this id"))
            } else {
                emit(Resource.Error(message = INVALID_TOKEN))
            }
        }
    }

    fun setUp(todoList: List<Todo>) {
        todoList.forEach {
            todos.add(it)
        }
    }
}