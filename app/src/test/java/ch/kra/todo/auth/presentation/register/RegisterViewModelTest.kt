package ch.kra.todo.auth.presentation.register

import android.content.Context
import app.cash.turbine.test
import ch.kra.todo.MainCoroutineRule
import ch.kra.todo.auth.data.repository.FakeAuthRepository
import ch.kra.todo.auth.domain.use_case.*
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.FakeSettingsDataStoreImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class RegisterViewModelTest {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeSettingsDataStoreImpl: FakeSettingsDataStoreImpl

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        fakeSettingsDataStoreImpl = FakeSettingsDataStoreImpl()
        registerViewModel = RegisterViewModel(
            Register(fakeAuthRepository),
            fakeSettingsDataStoreImpl,
            ValidateUsername(),
            ValidateEmail(),
            ValidateRegisterPassword(),
            ValidateRepeatedRegisterPassword()
        )
    }

    @Test
    fun `Set Username, username changed`() {
        val username = "kevin"
        assertEquals("Username is not empty", "", registerViewModel.registerFormState.value.username)
        registerViewModel.onEvent(RegisterListEvent.EnteredUsername(username))
        assertEquals("Username is not correct", username, registerViewModel.registerFormState.value.username)
    }

    @Test
    fun `Set Email, email changed`() {
        val email = "kevin@test.com"
        assertEquals("Email is not empty", "", registerViewModel.registerFormState.value.email)
        registerViewModel.onEvent(RegisterListEvent.EnteredEmail(email))
        assertEquals("Email is not correct", email, registerViewModel.registerFormState.value.email)
    }

    @Test
    fun `Set Password, password changed`() {
        val password = "Test1234+"
        assertEquals("Password is not empty", "", registerViewModel.registerFormState.value.password1)
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword1(password))
        assertEquals("Password is not correct", password, registerViewModel.registerFormState.value.password1)
    }

    @Test
    fun `Set RepeatedPassword, repeatedPassword changed`() {
        val password = "Test1234+"
        assertEquals("RepeatedPassword is not empty", "", registerViewModel.registerFormState.value.password2)
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword2(password))
        assertEquals("RepeatedPassword is not correct", password, registerViewModel.registerFormState.value.password2)
    }

    @Test
    fun `Toggle password visibility, passwordVisibility changed`() {
        val visibility = registerViewModel.registerFormState.value.passwordVisibility
        registerViewModel.onEvent(RegisterListEvent.TogglePasswordVisibility)
        assertEquals("Password visibility has not changed", !visibility, registerViewModel.registerFormState.value.passwordVisibility)
    }

    @Test
    fun `Register incorrect data, data errors are set`() {
        assertNull("Username error is not null", registerViewModel.registerFormState.value.usernameError)
        assertNull("Email error is not null", registerViewModel.registerFormState.value.emailError)
        assertNull("Password error is not null", registerViewModel.registerFormState.value.password1Error)
        assertNull("RepeatedPassword error is not null", registerViewModel.registerFormState.value.password2Error)
        registerViewModel.onEvent(RegisterListEvent.EnteredEmail("test"))
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword1("test"))
        registerViewModel.onEvent(RegisterListEvent.Register)
        assertNotNull("Username error is not set", registerViewModel.registerFormState.value.usernameError)
        assertNotNull("Email error is not set", registerViewModel.registerFormState.value.emailError)
        assertNotNull("Password error is not set", registerViewModel.registerFormState.value.password1Error)
        assertNotNull("Repeated password error is not set", registerViewModel.registerFormState.value.password2Error)
    }

    @Test
    fun `Register already existing username or email, apiError is set`() = runBlocking {
        val mockContext = mock<Context>()
        assertEquals("apiError is not empty", "", registerViewModel.apiError.value.asString(mockContext))
        registerViewModel.onEvent(RegisterListEvent.EnteredUsername("kevin"))
        registerViewModel.onEvent(RegisterListEvent.EnteredEmail("kevin@test.com"))
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword1("Test1234+"))
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword2("Test1234+"))
        registerViewModel.onEvent(RegisterListEvent.Register)

        assertNull("Username error is not null", registerViewModel.registerFormState.value.usernameError)
        assertNull("Email error is not null", registerViewModel.registerFormState.value.emailError)
        assertNull("Password error is not null", registerViewModel.registerFormState.value.password1Error)
        assertNull("RepeatedPassword error is not null", registerViewModel.registerFormState.value.password2Error)

        assertEquals("apiError is not correct", "Username or email already existing", registerViewModel.apiError.value.asString(mockContext))
    }

    @Test
    fun `Register free username and email, Token and Connecter user are set and Navigate to TodoList is send`() = runBlocking {
        registerViewModel.onEvent(RegisterListEvent.EnteredUsername("success"))
        registerViewModel.onEvent(RegisterListEvent.EnteredEmail("success@test.com"))
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword1("Success1+"))
        registerViewModel.onEvent(RegisterListEvent.EnteredPassword2("Success1+"))
        registerViewModel.onEvent(RegisterListEvent.Register)

        assertNull("Username error is not null", registerViewModel.registerFormState.value.usernameError)
        assertNull("Email error is not null", registerViewModel.registerFormState.value.emailError)
        assertNull("Password error is not null", registerViewModel.registerFormState.value.password1Error)
        assertNull("RepeatedPassword error is not null", registerViewModel.registerFormState.value.password2Error)

        fakeSettingsDataStoreImpl.preferenceFlow.test {
            val connections = awaitItem()
            assertEquals("Token is not correct", "success", connections.token)
            assertEquals("Connected user is not correct", "success", connections.connectedUser)
            awaitComplete()
        }

        registerViewModel.uiEvent.test {
            val uiEvent = awaitItem()
            assertTrue("uiEvent is not Navigate", uiEvent is UIEvent.Navigate)

            if (uiEvent is UIEvent.Navigate)
                assertEquals("uiEvent route is not correct", Routes.TODO_LIST, uiEvent.route)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Navigate back is called, PopBackStack is send`() = runBlocking {
        registerViewModel.onEvent(RegisterListEvent.NavigateBack)
        registerViewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue("Event is not PopBackStack", event is UIEvent.PopBackStack)
            cancelAndConsumeRemainingEvents()
        }
    }
}