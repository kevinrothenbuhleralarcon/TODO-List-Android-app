package ch.kra.todo.todo.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants.INVALID_TOKEN
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.TodoApi
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class TodoRepositoryImpl(
    private val todoApi: TodoApi
): TodoRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodoList(token: String): Flow<Resource<List<Todo>>> = flow {
        emit(Resource.Loading())
        try {
            val todoList = todoApi.getTodoList(token).todos.map { it.toTodo() }
            emit(Resource.Success(data = todoList))
        } catch (e: HttpException) {
            if (e.response()?.code() == 401) {
                emit(Resource.Error(message = INVALID_TOKEN))
            } else {
                emit(Resource.Error(
                    message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
                ))
            }
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodo(token: String, todoId: Int): Flow<Resource<Todo>> = flow {
        emit(Resource.Loading())
        try {
            val todo = todoApi.getTodo(token, todoId).todo.toTodo()
            emit(Resource.Success(data = todo))
        } catch (e: HttpException) {
            if (e.response()?.code() == 401) {
                emit(Resource.Error(message = INVALID_TOKEN))
            } else {
                emit(Resource.Error(
                    message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
                ))
            }
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }

    override fun addTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = todoApi.addTodo(token, request)
            if (response.isSuccessful) {
                emit(Resource.Success(data = response.body() ?: "Ok"))
            } else if (response.code() == 401) {
                emit(Resource.Error(message = INVALID_TOKEN))
            } else {
                emit(Resource.Error(message = response.body() ?: "Error"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }

    override fun updateTodo(token: String, request: AddEditTodoRequestDTO): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = todoApi.updateTodo(token, request)
            if (response.isSuccessful) {
                emit(Resource.Success(data = response.body() ?: "Ok"))
            } else if (response.code() == 401) {
                emit(Resource.Error(message = INVALID_TOKEN))
            } else {
                emit(Resource.Error(message = response.body() ?: "Error"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }

    override fun deleteTodo(token: String, todoId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = todoApi.deleteTodo(token, todoId)
            if (response.isSuccessful) {
                emit(Resource.Success(data = response.body() ?: "Ok"))
            } else if (response.code() == 401) {
                emit(Resource.Error(message = INVALID_TOKEN))
            } else {
                emit(Resource.Error(message = response.body() ?: "Error"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = e.message ?: "Could not reach server, check your internet connection."
            ))
        }
    }
}