package ch.kra.todo.auth.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.R
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.auth.domain.use_case.ValidatePassword
import ch.kra.todo.auth.domain.use_case.ValidateUsername
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.UIText
import ch.kra.todo.core.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: Login,
    private val settingsDataStore: SettingsDataStore,
    private val validateUsername: ValidateUsername,
    private val validatePassword: ValidatePassword
) : ViewModel() {

    private val _loginFormState = mutableStateOf(LoginFormState())
    val loginFormState: State<LoginFormState> = _loginFormState

    private val _apiError = mutableStateOf<UIText>(UIText.DynamicString(""))
    val apiError: State<UIText> = _apiError

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AuthListEvent) {
        when (event) {
            is AuthListEvent.EnteredUsername -> {
                _loginFormState.value = loginFormState.value.copy(
                    username = event.value
                )
            }

            is AuthListEvent.EnteredPassword -> {
                _loginFormState.value = loginFormState.value.copy(
                    password = event.value
                )
            }

            is AuthListEvent.Login -> {
                submitData()
            }

            is AuthListEvent.OnNavigateToWebClient -> {

            }

            is AuthListEvent.TogglePasswordVisibility -> {
                _loginFormState.value = loginFormState.value.copy(
                    passwordVisibility = !loginFormState.value.passwordVisibility
                )
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun submitData() {
        _loginFormState.value = loginFormState.value.copy(
            usernameError = null,
            passwordError = null
        )
        val usernameResult = validateUsername(loginFormState.value.username)
        val passwordResult = validatePassword(loginFormState.value.password)

        val hasError = listOf(
            usernameResult,
            passwordResult
        ).any { !it.sucessful }

        if (hasError) {
            _loginFormState.value = loginFormState.value.copy(
                usernameError = usernameResult.errorMessage,
                passwordError = passwordResult.errorMessage
            )
        } else {
            sendLoginRequest()
        }
    }

    private fun sendLoginRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            login(loginFormState.value.username, loginFormState.value.password)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Store the token and username in the dataStore
                            settingsDataStore.saveTokenToPreferenceStore(result.data.token)
                            settingsDataStore.saveConnectedUserToPreferenceStore(result.data.username)

                            sendUIEvent(
                                UIEvent.Navigate(
                                    Routes.TODO_LIST
                                )
                            )
                        }

                        is Resource.Error -> {
                            if (result.message.isNotEmpty()) {
                                _apiError.value = UIText.DynamicString(result.message)
                            } else {
                                _apiError.value = UIText.StringResource(R.string.io_error)
                            }
                        }

                        is Resource.Loading -> {
                            sendUIEvent(UIEvent.DisplayLoading)
                        }
                    }
                }.launchIn(this)
        }
    }
}