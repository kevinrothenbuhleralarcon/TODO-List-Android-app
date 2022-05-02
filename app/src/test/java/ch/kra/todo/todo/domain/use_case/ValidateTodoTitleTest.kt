package ch.kra.todo.todo.domain.use_case

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ValidateTodoTitleTest {
    private lateinit var validateTodoTitle: ValidateTodoTitle

    @Before
    fun setUp() {
        validateTodoTitle = ValidateTodoTitle()
    }

    @Test
    fun `Title is empty, return error`() {
        val result = validateTodoTitle("")
        assertEquals(result.successful, false)
    }

    @Test
    fun `Title is not empty, return success`() {
        val result = validateTodoTitle("Todo 1")
        assertEquals(result.successful, true)
    }
}