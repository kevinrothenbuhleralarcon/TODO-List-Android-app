package ch.kra.todo.todo.domain.use_case

import ch.kra.todo.todo.presentation.add_edit_todo.TaskFormState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateTaskEmptyTest {
    private lateinit var validateTaskEmpty: ValidateTaskEmpty

    @Before
    fun setUp() {
        validateTaskEmpty = ValidateTaskEmpty()
    }

    @Test
    fun `Task list is empty, returns error`() {
        val result = validateTaskEmpty(emptyList())
        assertEquals(result.sucessful, false)
    }

    @Test
    fun `Task list is not empty, return success`() {
        val tasks = listOf(
            TaskFormState(),
            TaskFormState()
        )
        val result = validateTaskEmpty(tasks)
        assertEquals(result.sucessful, true)
    }
}