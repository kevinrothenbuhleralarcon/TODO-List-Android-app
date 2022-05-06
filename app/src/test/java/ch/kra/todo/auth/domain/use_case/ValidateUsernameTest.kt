package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateUsernameTest {
    private lateinit var validateUsername: ValidateUsername

    @Before
    fun setUp() {
        validateUsername = ValidateUsername()
    }

    @Test
    fun `Username is empty, return error`() {
        val result = validateUsername("")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Username is not empty, return success`() {
        val result = validateUsername("kevin")
        assertEquals("Result is not success", true, result.successful)
    }
}