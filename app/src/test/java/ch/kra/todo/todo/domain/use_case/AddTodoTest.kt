package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.core.Constants.INVALID_TOKEN
import ch.kra.todo.core.Resource
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.data.repository.FakeTodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddTodoTest {

    private lateinit var addTodo: AddTodo
    private lateinit var fakeTodoRepository: FakeTodoRepository

    @Before
    fun setUp() {
        fakeTodoRepository = FakeTodoRepository()
        addTodo = AddTodo(fakeTodoRepository)
    }

    @Test
    fun `Invalid token, return error`() = runBlocking {
        val flow = addTodo(
            token = "notSuccess",
            AddEditTodoRequestDTO(
                TodoDTO(
                    title = "Test Todo 1",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("loading is not Loading", true, loading is Resource.Loading)

        val error = flow.last()
        assertEquals("error is not Error", true, error is Resource.Error)

        if (error is Resource.Error)
            assertEquals("error message is not $INVALID_TOKEN", INVALID_TOKEN, error.message)
    }

    @Test
    fun `Valid token, return success`() = runBlocking {
        val flow = addTodo(
            token = "success",
            AddEditTodoRequestDTO(
                TodoDTO(
                    title = "Test Todo 1",
                    createdAt = "2022-05-02T06:53:22.231Z",
                    lastUpdatedAt = "2022-05-02T06:53:22.231Z"
                )
            )
        )
        val loading = flow.first()
        assertEquals("result is not Loading", true, loading is Resource.Loading)

        val success = flow.last()
        assertEquals("success is not Success", true, success is Resource.Success)

        if (success is Resource.Success)
            assertEquals("success data is not Todo added", "Todo added", success.data)
    }
}