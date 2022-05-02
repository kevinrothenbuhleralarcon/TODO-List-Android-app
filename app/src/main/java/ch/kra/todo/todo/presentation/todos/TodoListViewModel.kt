package ch.kra.todo.todo.presentation.todos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.R
import ch.kra.todo.core.*
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.todo.domain.use_case.GetTodoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodoList: GetTodoList,
    private val settingsDataStore: SettingsDataStore
): ViewModel() {

    private val _token = mutableStateOf("")
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _todoListState = mutableStateOf(TodoListState())
    val todoListState: State<TodoListState> = _todoListState

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.AddTodo -> {
                sendUIEvent(
                    UIEvent.Navigate(Routes.ADD_EDIT_TODO)
                )
            }

            is TodoListEvent.EditTodo -> {
                sendUIEvent(
                    UIEvent.Navigate(
                        Routes.ADD_EDIT_TODO +
                                "?todoId=${event.todoId}"
                    )
                )
            }

            is TodoListEvent.Disconnect -> {
                /* TODO */
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodoList() {
        viewModelScope.launch {
            getTodoList(_token.value)
                .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _todoListState.value = todoListState.value.copy(
                            todoList = result.data,
                            isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _todoListState.value = todoListState.value.copy(
                            todoList = result.data ?: emptyList(),
                            isLoading = false
                        )
                        if (result.message == Constants.INVALID_TOKEN) {
                            sendUIEvent(UIEvent.Navigate(
                                Routes.LOGIN
                            ))
                        } else {
                            sendUIEvent(UIEvent.ShowSnackbar(
                                if (result.message.isNotEmpty()) UIText.DynamicString(result.message)
                                else UIText.StringResource(R.string.io_error)
                            ))
                        }
                    }

                    is Resource.Loading -> {
                        _todoListState.value = todoListState.value.copy(
                            todoList = result.data ?: emptyList(),
                            isLoading = true
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.preferenceFlow.collect {
                _token.value = it.token
                _username.value = it.connectedUser
            }
        }
    }
}