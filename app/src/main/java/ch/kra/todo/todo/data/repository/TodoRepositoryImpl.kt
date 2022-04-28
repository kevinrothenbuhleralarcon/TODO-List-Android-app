package ch.kra.todo.todo.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.TodoApi
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
            emit(Resource.Error(
                message = e.response()?.errorBody()?.charStream()?.readText() ?: e.message()
            ))
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