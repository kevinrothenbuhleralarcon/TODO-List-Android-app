package ch.kra.todo.auth.presentation.login

import ch.kra.todo.auth.data.repository.FakeAuthRepository
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.auth.domain.use_case.ValidateLoginPassword
import ch.kra.todo.auth.domain.use_case.ValidateUsername
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        fakeSettingsDataStoreImpl = FakeSettingsDataStoreImpl()
        loginViewModel = LoginViewModel(
            Login(fakeAuthRepository),
            fakeSettingsDataStoreImpl,
            ValidateUsername(),
            ValidateLoginPassword()
        )
    }

    @Test
    fun `Username change, update username`() {
        assertEquals("Username not empty", "", loginViewModel.loginFormState.value.username)
        val username = "kevin"
        loginViewModel.onEvent(LoginListEvent.EnteredUsername(username))
        assertEquals("Username not updated", username, loginViewModel.loginFormState.value.username)
    }

    @Test
    fun `Password change, update password`() {
        assertEquals("Password not empty", "", loginViewModel.loginFormState.value.password)
        val password = "kevin"
        loginViewModel.onEvent(LoginListEvent.EnteredPassword(password))
        assertEquals("Password not updated", password, loginViewModel.loginFormState.value.password)
    }

    @Test
    fun `Login with empty username, usernameError is set`() {
        assertEquals("usernameError is not null", null, loginViewModel.loginFormState.value.usernameError)
        loginViewModel.onEvent(LoginListEvent.Login)
        assertNotNull("usernameError is null", loginViewModel.loginFormState.value.usernameError)
    }

    @Test
    fun `Login with empty password, passwordError is set`() {
        assertEquals("passwordError is not null", null, loginViewModel.loginFormState.value.passwordError)
        loginViewModel.onEvent(LoginListEvent.Login)
        assertNotNull("passwordError is null", loginViewModel.loginFormState.value.passwordError)
    }
}