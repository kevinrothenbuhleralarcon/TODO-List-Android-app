package ch.kra.todo.todo.data.repository

import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.FakeTodoApi
import ch.kra.todo.todo.data.remote.dto.TodoDTO
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
    }

    @Test
    fun `GetTodoList valid Token, return success`() = runBlocking {
        val todos = mutableListOf(
            TodoDTO(id = 1, title = "todo 1", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z"),
            TodoDTO(id = 2, title = "todo 2", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z"),
            TodoDTO(id = 3, title = "todo 3", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z"),
            TodoDTO(id = 4, title = "todo 4", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z"),
            TodoDTO(id = 5, title = "todo 5", createdAt = "2022-05-02T06:53:22.231Z", lastUpdatedAt = "2022-05-02T06:53:22.231Z")
        )
        fakeTodoApi.todoList = todos


        val flow = todoRepositoryImpl.getTodoList(token = "success")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)

        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success)
            assertEquals("Result.data has not the correct size", todos.size, result.data.size)
    }
}