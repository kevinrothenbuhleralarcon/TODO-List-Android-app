package ch.kra.todo.todo.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateTaskDescriptionTest {
    private lateinit var validateTaskDescription: ValidateTaskDescription

    @Before
    fun setUp() {
        validateTaskDescription = ValidateTaskDescription()
    }

    @Test
    fun `Description is empty, return error`() {
        val result = validateTaskDescription("")
        assertEquals(result.successful, false)
    }

    @Test
    fun `Description is not empty, return success`() {
        val result = validateTaskDescription("Task 1")
        assertEquals(result.successful, true)
    }
}