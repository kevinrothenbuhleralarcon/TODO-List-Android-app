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
        assertEquals(result.successful, false)
    }

    /*@Test
    fun `email has wrong patter, return error`() {
        val result = validateEmail("kevin")
        assertEquals(result.successful, false)
    }

    @Test
    fun `email is not empty, return success`() {
        val result = validateEmail("kevin@test.com")
        assertEquals(result.successful, true)
    }*/
}