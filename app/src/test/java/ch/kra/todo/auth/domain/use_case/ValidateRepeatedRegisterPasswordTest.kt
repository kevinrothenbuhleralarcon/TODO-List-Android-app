package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateRepeatedRegisterPasswordTest {

    private lateinit var validateRepeatedRegisterPassword: ValidateRepeatedRegisterPassword

    @Before
    fun setUp() {
        validateRepeatedRegisterPassword = ValidateRepeatedRegisterPassword()
    }

    @Test
    fun `Passwords does not match, return error`() {
        val result = validateRepeatedRegisterPassword("test", "test2")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Passwords match, return success`() {
        val result = validateRepeatedRegisterPassword("test", "test")
        assertEquals("Result is not success", true, result.successful)
    }
}