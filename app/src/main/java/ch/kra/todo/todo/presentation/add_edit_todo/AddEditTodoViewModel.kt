package ch.kra.todo.todo.presentation.add_edit_todo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.Resource
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.todo.data.remote.dto.TaskDTO
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.use_case.AddTodo
import ch.kra.todo.todo.domain.use_case.DeleteTodo
import ch.kra.todo.todo.domain.use_case.GetTodo
import ch.kra.todo.todo.domain.use_case.UpdateTodo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val getTodo: GetTodo,
    private val settingsDataStore: SettingsDataStore,
    private val addTodo: AddTodo,
    private val updateTodo: UpdateTodo,
    private val deleteTodo: DeleteTodo,
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.Save -> {
                /* TODO validate form */
                _currentTodoId?.let {
                    updateTodo()
                } ?: addTodo()
            }

            is AddEditTodoEvent.Delete -> {
                deleteTodo()
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

            is AddEditTodoEvent.AddTask -> {
                val tasks = state.tasks.toMutableList()
                tasks.add(TaskFormState())
                state = state.copy(tasks = tasks)
            }

            is AddEditTodoEvent.RemoveTask -> {
                val tasks = state.tasks.toMutableList()
                tasks.removeAt(event.id)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTodo() {
        val tasks = state.tasks.map { task -> TaskDTO(
            id = task.id,
            description = task.description,
            deadline = task.deadline?.let { DateFormatUtil.toISOInstantString(it) },
            status = task.status,
            todoId = null
        ) }
        val todo = TodoDTO(
            id = null,
            title = state.title,
            createdAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            lastUpdatedAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            tasks = tasks
        )
        viewModelScope.launch {
            addTodo(_token.value, AddEditTodoRequestDTO(todo))
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            sendUIEvent(UIEvent.PopBackStack)
                        }

                        is Resource.Error -> {
                            /* TODO */
                            Log.d("addTodo", result.message ?: "Error")
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTodo() {
        val tasks = state.tasks.map { task -> TaskDTO(
            id = task.id,
            description = task.description,
            deadline = task.deadline?.let { DateFormatUtil.toISOInstantString(it) },
            status = task.status,
            todoId = _currentTodoId
        ) }
        val todo = TodoDTO(
            id = _currentTodoId,
            title = state.title,
            createdAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            lastUpdatedAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            tasks = tasks
        )
        viewModelScope.launch {
            updateTodo(_token.value, AddEditTodoRequestDTO(todo))
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            sendUIEvent(UIEvent.PopBackStack)
                        }

                        is Resource.Error -> {
                            /* TODO */
                            Log.d("updateTodo", result.message ?: "Error")
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

    private fun deleteTodo() {
        _currentTodoId?.let {
            viewModelScope.launch {
                deleteTodo(_token.value, it)
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                sendUIEvent(UIEvent.PopBackStack)
                            }

                            is Resource.Error -> {
                                /* TODO */
                                Log.d("deleteTodo", result.message ?: "Error")
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