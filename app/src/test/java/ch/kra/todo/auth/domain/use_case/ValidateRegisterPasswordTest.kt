package ch.kra.todo.auth.domain.use_case

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateRegisterPasswordTest {

    private lateinit var validateRegisterPassword: ValidateRegisterPassword

    @Before
    fun setUp() {
        validateRegisterPassword = ValidateRegisterPassword()
    }

    @Test
    fun `Password is empty, return error`() {
        val result = validateRegisterPassword("")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Password has incorrect number of character, return error`() {
        val result = validateRegisterPassword("Test+")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Password has does not have a capital letter, return error`() {
        val result = validateRegisterPassword("test1234+")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Password has does not have a special character, return error`() {
        val result = validateRegisterPassword("Test1234")
        assertEquals("Result is not error", false, result.successful)
    }

    @Test
    fun `Password has correct pattern, return success`() {
        val specialChars = listOf(
            "*",
            "!",
            "@",
            "#",
            "%",
            "^",
            "&",
            "(",
            ")",
            "{",
            "}",
            ":",
            ";",
            "<",
            ">",
            ",",
            ".",
            "?",
            "/",
            "~",
            "_",
            "+",
            "=",
            "|",
            "[",
            "]",
            "\\",
            "-"
        )
        specialChars.forEach {
            val result = validateRegisterPassword("tesT123$it")
            assertEquals("Result is not success", true, result.successful)
        }
    }
}