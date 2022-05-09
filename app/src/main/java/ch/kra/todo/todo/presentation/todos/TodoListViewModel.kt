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
import ch.kra.todo.core.data.local.SettingsDataStoreImpl
import ch.kra.todo.todo.domain.use_case.GetTodoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val getTodoList: GetTodoList,
    private val settingsDataStoreImpl: SettingsDataStore
): ViewModel() {

    val preferences = settingsDataStoreImpl.preferenceFlow

    private val _todoListState = mutableStateOf(TodoListState())
    val todoListState: State<TodoListState> = _todoListState

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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

            is TodoListEvent.Refresh -> {
                getTodoList()
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
            preferences.collectLatest {
                getTodoList(it.token)
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
    }
}