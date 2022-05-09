package ch.kra.todo.auth.data.remote.dto.responses

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserDTOTest {

    private lateinit var userDTO: UserDTO

    @Before
    fun setUp() {
        userDTO = UserDTO(
            username = "Kevin",
            email = "kevin@test.com"
        )
    }

    @Test
    fun `to User, return new User`() {
        val user = userDTO.toUser()
        assertEquals("Username is not correct", userDTO.username, user.username)
        assertEquals("Email is not correct", userDTO.email, user.email)
    }
}