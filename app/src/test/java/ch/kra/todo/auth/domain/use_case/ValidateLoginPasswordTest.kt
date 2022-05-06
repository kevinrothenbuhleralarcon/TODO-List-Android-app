package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateLoginPasswordTest {
    private lateinit var validateLoginPassword: ValidateLoginPassword

    @Before
    fun setUp() {
        validateLoginPassword = ValidateLoginPassword()
    }

    @Test
    fun `Password is empty, return error`() {
        val result = validateLoginPassword("")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Password is not empty, return success`() {
        val result = validateLoginPassword("Test")
        assertEquals("Result is not success", true, result.successful)
    }
}