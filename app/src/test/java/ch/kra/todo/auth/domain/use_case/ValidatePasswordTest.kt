package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidatePasswordTest {
    private lateinit var validatePassword: ValidatePassword

    @Before
    fun setUp() {
        validatePassword = ValidatePassword()
    }

    @Test
    fun `Password is empty, return error`() {
        val result = validatePassword("")
        assertEquals(result.successful, false)
    }

    @Test
    fun `Password is not empty, return success`() {
        val result = validatePassword("Test")
        assertEquals(result.successful, true)
    }
}