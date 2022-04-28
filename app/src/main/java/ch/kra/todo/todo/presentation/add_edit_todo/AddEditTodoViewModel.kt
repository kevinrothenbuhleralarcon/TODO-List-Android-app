package ch.kra.todo.todo.presentation.add_edit_todo

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.core.Resource
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.todo.domain.use_case.GetTodo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val getTodo: GetTodo,
    private val settingsDataStore: SettingsDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _token = mutableStateOf("")
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    var state by mutableStateOf(TodoFormState())

    private var _currentTodoId: Int? = null
    val currentTodoId get() = _currentTodoId

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.consumeAsFlow()

    init {
        loadSettings()
        savedStateHandle.get<Int>("todoId")?.let { todoId ->
            if (todoId != -1) {
                getTodo(todoId)
            }
        }
    }

    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.Save -> {

            }

            is AddEditTodoEvent.Delete -> {

            }

            is AddEditTodoEvent.NavigateBack -> {
                sendUIEvent(UIEvent.PopBackStack)
            }

            is AddEditTodoEvent.TitleChanged -> {
                state = state.copy(title = event.value)
            }

            is AddEditTodoEvent.DescriptionChanged -> {
                val tasks = state.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(description = event.value)
                state = state.copy(tasks = tasks)
            }

            is AddEditTodoEvent.StatusChanged -> {
                val tasks = state.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(status = event.value)
                state = state.copy(tasks = tasks)
            }

            is AddEditTodoEvent.DeadlineChanged -> {
                val tasks = state.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(deadline = event.value)
                state = state.copy(tasks = tasks)
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun getTodo(todoId: Int) {
        viewModelScope.launch {
            getTodo(_token.value, todoId)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _currentTodoId = it.id
                                state = state.copy(
                                    title = it.title,
                                    createdAt = it.createdAt,
                                    tasks = it.tasks?.map { task ->
                                        TaskFormState(
                                            id = task.id,
                                            description = task.description,
                                            status = task.status,
                                            deadline = task.deadline
                                        )
                                    } ?: emptyList(),
                                    isLoading = false
                                )
                            }
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false
                            )
                            sendUIEvent(UIEvent.ShowSnackbar(
                                message = result.message ?: "Unknown error"
                            ))
                        }

                        is Resource.Loading -> {
                            state = state.copy(
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