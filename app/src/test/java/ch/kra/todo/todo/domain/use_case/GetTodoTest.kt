package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.Constants
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.repository.FakeTodoRepository
import ch.kra.todo.todo.domain.model.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetTodoTest {

    private lateinit var getTodo: GetTodo
    private lateinit var fakeTodoRepository: FakeTodoRepository

    @Before
    fun setUp() {
        fakeTodoRepository = FakeTodoRepository()
        getTodo = GetTodo(fakeTodoRepository)

        val todos = listOf(
            Todo(id = 1, title = "Test Todo 1", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 2, title = "Test Todo 2", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 3, title = "Test Todo 3", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 4, title = "Test Todo 4", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList()),
            Todo(id = 5, title = "Test Todo 5", createdAt = DateFormatUtil.fromLong(1651474402231), lastUpdatedAt = DateFormatUtil.fromLong(1651474402231), tasks =  emptyList())
        )
        fakeTodoRepository.setUp(todos)
    }

    @Test
    fun `Invalid Token, return Error`() = runBlocking{
        val token = "notSuccess"
        val todoId = 3
        val loading = getTodo(token, todoId).first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val error = getTodo(token, todoId).last()
        assertEquals("error is not Resource.Error", true, error is Resource.Error)

        if (error is Resource.Error) {
            assertEquals("error.message is not ${Constants.INVALID_TOKEN}",
                Constants.INVALID_TOKEN, error.message)
        }
    }

    @Test
    fun `Invalid todoId, return Error`() = runBlocking{
        val token = "success"
        val todoId = 10
        val loading = getTodo(token, todoId).first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val error = getTodo(token, todoId).last()
        assertEquals("error is not Resource.Error", true, error is Resource.Error)

        if (error is Resource.Error) {
            assertEquals("error.message is not No todo with this id",
                "No todo with this id", error.message)
        }
    }

    @Test
    fun `Valid Token and valid todoId, return Success`() = runBlocking{
        val token = "success"
        val todoId = 3
        val loading = getTodo(token, todoId).first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val success = getTodo(token, todoId).last()
        assertEquals("success is not Resource.Success", true, success is Resource.Success)

        if (success is Resource.Success) {
            assertEquals("success.data.id is not todoId",
                todoId, success.data.id
            )
        }
    }
}