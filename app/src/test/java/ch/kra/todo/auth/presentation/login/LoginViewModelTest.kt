package ch.kra.todo.auth.presentation.login


import android.content.Context
import app.cash.turbine.test
import ch.kra.todo.MainCoroutineRule
import ch.kra.todo.auth.data.repository.FakeAuthRepository
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.auth.domain.use_case.ValidateLoginPassword
import ch.kra.todo.auth.domain.use_case.ValidateUsername
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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

    @Test
    fun `Login with incorrect username or password, return Error`() = runBlocking {
        val mockContext = mock<Context>()
        assertEquals("apiError is not empty", "", loginViewModel.apiError.value.asString(mockContext))
        loginViewModel.onEvent(LoginListEvent.EnteredUsername("incorrect"))
        loginViewModel.onEvent(LoginListEvent.EnteredPassword("incorrect"))
        loginViewModel.onEvent(LoginListEvent.Login)
        assertEquals("apiError has incorrect message", "Invalid username", loginViewModel.apiError.value.asString(mockContext))
    }

    @Test
    fun `Login with correct username and password, return Success`() = runBlocking {
        loginViewModel.onEvent(LoginListEvent.EnteredUsername("success"))
        loginViewModel.onEvent(LoginListEvent.EnteredPassword("success"))
        loginViewModel.onEvent(LoginListEvent.Login)
        fakeSettingsDataStoreImpl.preferenceFlow.test {
            val emission = awaitItem()
            assertEquals("the stored token has not the correct string", "success", emission.token)
            assertEquals("the stored username has not the correct string", "success", emission.connectedUser)
            awaitComplete()
        }

        loginViewModel.uiEvent.test {
            val emission = awaitItem()
            assertEquals("The UI event is not the correct one", true, emission is UIEvent.Navigate)
            if (emission is UIEvent.Navigate)
                assertEquals("The Route is not correct", Routes.TODO_LIST, emission.route)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Register is called, return navigate to Register`() = runBlocking {
        loginViewModel.onEvent(LoginListEvent.Register)
        loginViewModel.uiEvent.test {
            val emission = awaitItem()
            assertEquals("The UI event is not the correct one", true, emission is UIEvent.Navigate)
            if (emission is UIEvent.Navigate)
                assertEquals("The Route is not correct", Routes.REGISTER, emission.route)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Toggle password visibility password visibility is toggled`() {
        val visibility = loginViewModel.loginFormState.value.passwordVisibility
        loginViewModel.onEvent(LoginListEvent.TogglePasswordVisibility)
        assertEquals("Password visibility is not correct", !visibility, loginViewModel.loginFormState.value.passwordVisibility)
    }
}