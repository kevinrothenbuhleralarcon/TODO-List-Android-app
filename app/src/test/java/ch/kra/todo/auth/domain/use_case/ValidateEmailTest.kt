package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateEmailTest{
    private lateinit var validateEmail: ValidateEmail

    @Before
    fun setUp() {
        validateEmail = ValidateEmail()
    }

    @Test
    fun `email is empty, return error`() {
        val result = validateEmail("")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `email has wrong pattern, return error`() {
        val result = validateEmail("kevin")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `email has correct pattern, return success`() {
        val result = validateEmail("kevin@test.com")
        assertEquals("Result is not success", true, result.successful)
    }
}