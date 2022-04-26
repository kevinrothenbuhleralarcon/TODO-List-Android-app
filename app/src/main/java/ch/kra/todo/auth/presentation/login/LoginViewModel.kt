package ch.kra.todo.auth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: Login
): ViewModel() {

    init {
        sendLoginRequest("Kevin", "Test1234+")
    }
    private fun sendLoginRequest(username: String, password: String) {
        viewModelScope.launch {
            login(username, password)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("login", result.data.toString())
                        }

                        is Resource.Error -> {
                            Log.d("login", result.message!!)
                        }

                        is Resource.Loading -> {
                            Log.d("login", "Loading")
                        }
                    }
                }.launchIn(this)
        }
    }
}