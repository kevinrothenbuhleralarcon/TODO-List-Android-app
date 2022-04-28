package ch.kra.todo.todo.presentation.todos

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.Resource
import ch.kra.todo.core.Routes
import ch.kra.todo.core.UIEvent
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
        getTodoList()
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
                                "/${event.todoId}"
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
    private fun getTodoList() {
        viewModelScope.launch {
            getTodoList(_token.value)
                .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _todoListState.value = todoListState.value.copy(
                            todoList = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _todoListState.value = todoListState.value.copy(
                            todoList = result.data ?: emptyList(),
                            isLoading = false
                        )
                        /* TODO: Check if connection error and redirect */
                        sendUIEvent(UIEvent.ShowSnackbar(
                            result.message ?: "Unknown error"
                        ))
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