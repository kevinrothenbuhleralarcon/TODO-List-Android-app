package ch.kra.todo.auth.domain.use_case

import app.cash.turbine.test
import ch.kra.todo.auth.data.repository.FakeAuthRepository
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterTest {

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var register: Register

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        register = Register(fakeAuthRepository)
    }

    @Test
    fun `Register correct username, email and password, result success`() = runBlocking {
        val result = register("success", "success@test.com", "Success1+")
        result.test {
            val loading = awaitItem()
            assertEquals("result is not loading", loading is Resource.Loading, true)
            val success = awaitItem()
            assertEquals("result is not success", success is Resource.Success, true)
            awaitComplete()
        }
    }

    @Test
    fun `Login incorrect username, email and password, result error`() = runBlocking {
        val result = register("notsuccess", "notsuccess@test.com", "notsuccess")
        result.test {
            val loading = awaitItem()
            assertEquals("result is not loading", loading is Resource.Loading, true)
            val success = awaitItem()
            assertEquals("result is not error", success is Resource.Error, true)
            awaitComplete()
        }
    }
}