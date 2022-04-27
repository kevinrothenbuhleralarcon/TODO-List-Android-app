package ch.kra.todo.auth.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
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
    private val settingsDataStore: SettingsDataStore
): ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _passwordVisible = mutableStateOf(false)
    val passwordVisible: State<Boolean> = _passwordVisible

    private val _errors = mutableStateOf(listOf<String>())
    val errors: State<List<String>> = _errors


    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: AuthListEvent) {
        when (event) {
            is AuthListEvent.EnteredUsername -> {
                _username.value = event.value
            }

            is AuthListEvent.EnteredPassword -> {
                _password.value = event.value
            }

            is AuthListEvent.Login -> {

                _errors.value = validateForm()
                if(errors.value.isEmpty()) {
                    sendLoginRequest()
                }
            }

            is AuthListEvent.OnNavigateToWebClient -> {

            }

            is AuthListEvent.TogglePasswordVisibility -> {
                _passwordVisible.value = !passwordVisible.value
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun sendLoginRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            login(username.value, password.value)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Store the token and username in the dataStore
                            settingsDataStore.saveTokenToPreferenceStore(result.data?.token ?: "")
                            settingsDataStore.saveConnectedUserToPreferenceStore(result.data?.username ?: "")

                            sendUIEvent(UIEvent.Navigate(
                                Routes.TODO_LIST
                            ))
                        }

                        is Resource.Error -> {
                            _errors.value = listOf(result.message ?: "An error occured")
                        }

                        is Resource.Loading -> {
                            sendUIEvent(UIEvent.DisplayLoading)
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun validateForm(): List<String> {
        val validateErrors = mutableListOf<String>()
        if(username.value.isEmpty()) validateErrors.add("Username cannot be empty")
        if(password.value.isEmpty()) validateErrors.add("Password cannot be empty")
        return validateErrors
    }
}