package ch.kra.todo.auth.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.R
import ch.kra.todo.auth.domain.use_case.*
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.UIText
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.core.data.local.SettingsDataStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: Register,
    private val settingsDataStoreImpl: SettingsDataStore,
    private val validateUsername: ValidateUsername,
    private val validateEmail: ValidateEmail,
    private val validateRegisterPassword: ValidateRegisterPassword,
    private val validateRepeatedRegisterPassword: ValidateRepeatedRegisterPassword
) : ViewModel() {

    private val _registerFormState = mutableStateOf(RegisterFormState())
    val registerFormState: State<RegisterFormState> = _registerFormState

    private val _apiError = mutableStateOf<UIText>(UIText.DynamicString(""))
    val apiError: State<UIText> = _apiError

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RegisterListEvent) {
        when (event) {
            is RegisterListEvent.EnteredUsername -> {
                _registerFormState.value = registerFormState.value.copy(
                    username = event.value
                )
            }

            is RegisterListEvent.EnteredEmail -> {
                _registerFormState.value = registerFormState.value.copy(
                    email = event.value
                )
            }

            is RegisterListEvent.EnteredPassword1 -> {
                _registerFormState.value = registerFormState.value.copy(
                    password1 = event.value
                )
            }

            is RegisterListEvent.EnteredPassword2 -> {
                _registerFormState.value = registerFormState.value.copy(
                    password2 = event.value
                )
            }

            is RegisterListEvent.TogglePasswordVisibility -> {
                _registerFormState.value = registerFormState.value.copy(
                    passwordVisibility = !registerFormState.value.passwordVisibility
                )
            }

            is RegisterListEvent.Register -> {
                submitData()
            }

            is RegisterListEvent.NavigateBack -> {
                sendUIEvent(UIEvent.PopBackStack)
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun submitData() {
        _registerFormState.value = registerFormState.value.copy(
            usernameError = null,
            emailError = null,
            password1Error = null,
            password2Error = null
        )

        val usernameResult = validateUsername(registerFormState.value.username)
        val emailResult = validateEmail(registerFormState.value.email)
        val passwordResult = validateRegisterPassword(registerFormState.value.password1)
        val repeatedPasswordResult = validateRepeatedRegisterPassword(
            registerFormState.value.password1,
            registerFormState.value.password2
        )

        val hasError = listOf(
            usernameResult,
            emailResult,
            passwordResult,
            repeatedPasswordResult
        ).any { !it.successful }

        if (hasError) {
            _registerFormState.value = registerFormState.value.copy(
                usernameError = usernameResult.errorMessage,
                emailError = emailResult.errorMessage,
                password1Error = passwordResult.errorMessage,
                password2Error = passwordResult.errorMessage
            )
        } else {
            register()
        }
    }

    private fun register() {
        viewModelScope.launch {
            register(
                registerFormState.value.username,
                registerFormState.value.email,
                registerFormState.value.password1
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _registerFormState.value = _registerFormState.value.copy(
                            isLoading = false
                        )
                        // Store the token and username in the dataStore
                        settingsDataStoreImpl.saveTokenToPreferenceStore(result.data.token)
                        settingsDataStoreImpl.saveConnectedUserToPreferenceStore(result.data.username)

                        sendUIEvent(
                            UIEvent.Navigate(Routes.TODO_LIST)
                        )
                    }

                    is Resource.Error -> {
                        _registerFormState.value = _registerFormState.value.copy(
                            isLoading = false
                        )
                        if (result.message.isNotEmpty()) {
                            _apiError.value = UIText.DynamicString(result.message)
                        } else {
                            _apiError.value = UIText.StringResource(R.string.io_error)
                        }
                    }

                    is Resource.Loading -> {
                        _registerFormState.value = _registerFormState.value.copy(
                            isLoading = true
                        )
                    }
                }
            }.launchIn(this)
        }
    }
}