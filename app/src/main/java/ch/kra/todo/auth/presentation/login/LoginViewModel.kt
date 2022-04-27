package ch.kra.todo.auth.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: Login
): ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _password = mutableStateOf("")
    val password: State<String> = _password

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
                sendLoginRequest()
            }

            is AuthListEvent.OnNavigateToWebClient -> {

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
                            sendUIEvent(UIEvent.Navigate(
                                Routes.TODO_LIST
                            ))
                        }

                        is Resource.Error -> {
                            sendUIEvent(UIEvent.DisplayError(
                                result.message ?: "An error occured"
                            ))
                        }

                        is Resource.Loading -> {
                            sendUIEvent(UIEvent.DisplayLoading)
                        }
                    }
                }.launchIn(this)
        }
    }
}