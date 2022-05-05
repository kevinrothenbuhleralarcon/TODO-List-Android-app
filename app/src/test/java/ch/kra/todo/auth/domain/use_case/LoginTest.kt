package ch.kra.todo.auth.domain.use_case

import ch.kra.todo.auth.data.repository.FakeAuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginTest {

    private lateinit var login: Login
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        login = Login(fakeAuthRepository)
    }

    @Test
    fun `Login correct username and password, result success`() = runBlocking {
        val result = login("success", "success").first()
        assertEquals("result is not loading", result is Resource.Loading, true)

        val success = login("success", "success").last()
        assertEquals("result is not success", success is Resource.Success, true)
    }

    @Test
    fun `Login incorrect username and password, result error`() = runBlocking {
        val result = login("notsuccess", "notsuccess").first()
        assertEquals("result is not loading", result is Resource.Loading, true)

        val error = login("notsuccess", "notsuccess").last()
        assertEquals("result is not error", error is Resource.Error, true)
    }
}