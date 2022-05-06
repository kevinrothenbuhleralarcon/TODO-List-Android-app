package ch.kra.todo.auth.data.repository

import ch.kra.todo.auth.data.remote.FakeAuthApi
import ch.kra.todo.core.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var authRepositoryImpl: AuthRepositoryImpl
    private lateinit var fakeAuthApi: FakeAuthApi

    @Before
    fun setUp() {
        fakeAuthApi = FakeAuthApi()
        authRepositoryImpl = AuthRepositoryImpl(fakeAuthApi)
    }

    @Test
    fun `Login with valid credential, return Success`() = runBlocking {
        val flow = authRepositoryImpl.login("success", "success")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)
        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success)
            assertEquals("result.token is not success", "success", result.data.token)
    }

    @Test
    fun `Login with invalid credential, return Error`() = runBlocking {
        val flow = authRepositoryImpl.login("notSuccess", "notSuccess")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)
        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error)
            assertEquals("result.message is not correct", "Invalid Credential", result.message)
    }

    @Test
    fun `Register with valid account, return Success`() = runBlocking {
        val flow = authRepositoryImpl.register("success", "success", "success")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)
        val result = flow.last()
        assertEquals("result is not Resource.Success", true, result is Resource.Success)
        if (result is Resource.Success)
            assertEquals("result.token is not success", "success", result.data.token)
    }

    @Test
    fun `Register with existing account, return Error`() = runBlocking {
        val flow = authRepositoryImpl.register("existing", "existing", "existing")
        val loading = flow.first()
        assertEquals("loading is not Resource.Loading", true, loading is Resource.Loading)
        val result = flow.last()
        assertEquals("result is not Resource.Error", true, result is Resource.Error)
        if (result is Resource.Error)
            assertEquals("result.token is not Username already in use. Please login", "Username already in use. Please login", result.message)
    }
}