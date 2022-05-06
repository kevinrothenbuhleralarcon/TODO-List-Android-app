package ch.kra.todo.todo.data.repository

import ch.kra.todo.core.Constants.INVALID_TOKEN
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.FakeTodoApi
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TodoRepositoryImplTest {

    private lateinit var todoRepositoryImpl: TodoRepositoryImpl
    private lateinit var fakeTodoApi: FakeTodoApi

    @Before
    fun setUp() {
        fakeTodoApi = FakeTodoApi()
        todoRepositoryImpl = TodoRepositoryImpl(fakeTodoApi)

        val todos = mutableListOf(
            TodoDTO(
                id = 1,
                title = "todo 1",
                createdAt = "2022-05-02T06:53:22.231Z",
                lastUpdatedAt = "2022-05-02T06:53:22.231Z"
            ),
            TodoDTO(
                id = 2,
                title = "todo 2",
                createdAt = "2022-05-02T06:53:22.231Z",
                lastUpdatedAt = "2022-05-02T06:53:22.231Z"
            ),
            TodoDTO(
                id = 3,
                title = "todo 3",
                createdAt = "2022-05-02T06:53:22.231Z",
                lastUpdatedAt = "2022-05-02T06:53:22.231Z"
            ),
            TodoDTO(
                id = 4,
                title = "todo 4",
                createdAt = "2022-05-02T06:53:22.231Z",
                lastUpdatedAt = "2022-05-02T06:53:22.231Z"
            ),
            TodoDTO(
                id = 5,
                title = "todo 5",
                createdAt = "2022-05-02T06:53:22.231Z",
                lastUpdatedAt = "2022-05-02T06:53:22.231Z"
            )
        )
        fakeTodoApi.todoList = todos
    }

    @Test
    fun `GetTodoList valid Token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.getTodoList(token = "success")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success)
            assertEquals("Result.data has not the correct size", fakeTodoApi.todoList.size, result.data.size)
    }

    @Test
    fun `GetTodoList invalid Token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.getTodoList(token = "notSuccess")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error)
            assertEquals("result.message is not correct", INVALID_TOKEN, result.message)
    }

    @Test
    fun `GetTodo valid token and id, return Success`() = runBlocking {
        val flow = todoRepositoryImpl.getTodo(token = "success", todoId = 3)
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success) {
            assertEquals("result.data has not the correct id", fakeTodoApi.todoList[2].id, result.data.id)
            assertEquals("result.data has not the correct title", fakeTodoApi.todoList[2].title, result.data.title)
            assertEquals("result.data is not the correct todo", fakeTodoApi.todoList[2].toTodo(), result.data)
        }
    }

    @Test
    fun `GetTodo invalid token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.getTodo(token = "notSuccess", todoId = 3)
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error)
            assertEquals("result.message is not correct", INVALID_TOKEN, result.message)
    }

    @Test
    fun `GetTodo invalid id, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.getTodo(token = "success", todoId = 10)
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
    }

    @Test
    fun `AddTodo valid token and request, return Success`() = runBlocking {
        val flow = todoRepositoryImpl.addTodo(
            token = "success",
            request = AddEditTodoRequestDTO(
                TodoDTO(
                    title = "success",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)

        if (result is Resource.Success)
            assertEquals("result.data is not correct", "New todo added", result.data)
    }

    @Test
    fun `AddTodo error, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.addTodo(
            token = "success",
            request = AddEditTodoRequestDTO(
                TodoDTO(
                    title = "error",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)

        if (result is Resource.Error)
            assertEquals(
                "result.message is not correct",
                "Failed to insert the new todo",
                result.message
            )
    }

    @Test
    fun `AddTodo invalid token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.addTodo(
            token = "notSuccess",
            request = AddEditTodoRequestDTO(
                TodoDTO(
                    title = "success",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)

        if (result is Resource.Error)
            assertEquals("result.message is not correct", INVALID_TOKEN, result.message)
    }

    @Test
    fun `UpdateTodo valid token and request, return Success`() = runBlocking {
        val flow = todoRepositoryImpl.updateTodo(
            token = "success",
            AddEditTodoRequestDTO(
                TodoDTO(
                    id = 3,
                    title = "updated todo 3",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success) {
            assertEquals("result.data is not correct", "Todo updated", result.data)
        }
        assertEquals("the todo is not correctly updated", "updated todo 3", fakeTodoApi.todoList[2].title)
    }

    @Test
    fun `UpdateTodo invalid token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.updateTodo(
            token = "notSuccess",
            AddEditTodoRequestDTO(
                TodoDTO(
                    id = 3,
                    title = "updated todo 3",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error) {
            assertEquals("result.message is not correct", INVALID_TOKEN, result.message)
        }
    }

    @Test
    fun `UpdateTodo invalid request, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.updateTodo(
            token = "success",
            AddEditTodoRequestDTO(
                TodoDTO(
                    id = 10,
                    title = "updated todo 3",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error) {
            assertEquals("result.message is not correct", "Update failed", result.message)
        }
    }

    @Test
    fun `DeleteTodo valid token and id, return Success`() = runBlocking {
        val flow = todoRepositoryImpl.deleteTodo("success", 3)

        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success) {
            assertEquals("result.data is not correct", "Todo deleted", result.data)
        }
    }

    @Test
    fun `DeleteTodo invalid token, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.deleteTodo("notSuccess", 3)

        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error) {
            assertEquals("result.message is not correct", INVALID_TOKEN, result.message)
        }
    }

    @Test
    fun `DeleteTodo invalid id, return Error`() = runBlocking {
        val flow = todoRepositoryImpl.deleteTodo("success", 10)

        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error) {
            assertEquals("result.message is not correct", "No todo with this id to delete for the connected user", result.message)
        }
    }
}