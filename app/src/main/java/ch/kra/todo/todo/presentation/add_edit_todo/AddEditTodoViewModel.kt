package ch.kra.todo.todo.presentation.add_edit_todo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.kra.todo.R
import ch.kra.todo.core.*
import ch.kra.todo.core.Constants.INVALID_TOKEN
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.core.data.local.SettingsDataStoreImpl
import ch.kra.todo.todo.data.remote.dto.TaskDTO
import ch.kra.todo.todo.data.remote.dto.TodoDTO
import ch.kra.todo.todo.data.remote.dto.request.AddEditTodoRequestDTO
import ch.kra.todo.todo.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val getTodo: GetTodo,
    private val settingsDataStoreImpl: SettingsDataStore,
    private val addTodo: AddTodo,
    private val updateTodo: UpdateTodo,
    private val deleteTodo: DeleteTodo,
    private val validateTaskDescription: ValidateTaskDescription,
    private val validateTodoTitle: ValidateTodoTitle,
    private val validateTaskEmpty: ValidateTaskEmpty,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val preferences = settingsDataStoreImpl.preferenceFlow

    private val _todoFormState = mutableStateOf(TodoFormState())
    val todoFormState: State<TodoFormState> = _todoFormState

    private val _apiError = mutableStateOf<UIText>(UIText.DynamicString(""))
    val apiError: State<UIText> = _apiError

    private var _currentTodoId: Int? = null
    val currentTodoId get() = _currentTodoId

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        savedStateHandle.get<Int>("todoId")?.let { todoId ->
            if (todoId > -1) {
                getTodo(todoId)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.Save -> {
                submitData()
            }

            is AddEditTodoEvent.Delete -> {
                deleteTodo()
            }

            is AddEditTodoEvent.NavigateBack -> {
                sendUIEvent(UIEvent.PopBackStack)
            }

            is AddEditTodoEvent.TitleChanged -> {
                _todoFormState.value = _todoFormState.value.copy(title = event.value)
            }

            is AddEditTodoEvent.DescriptionChanged -> {
                val tasks = _todoFormState.value.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(description = event.value)
                _todoFormState.value = _todoFormState.value.copy(tasks = tasks)
            }

            is AddEditTodoEvent.StatusChanged -> {
                val tasks = _todoFormState.value.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(status = event.value)
                _todoFormState.value = _todoFormState.value.copy(tasks = tasks)
            }

            is AddEditTodoEvent.DeadlineChanged -> {
                val tasks = _todoFormState.value.tasks.toMutableList()
                tasks[event.id] = tasks[event.id].copy(deadline = event.value)
                _todoFormState.value = _todoFormState.value.copy(tasks = tasks)
            }

            is AddEditTodoEvent.AddTask -> {
                val tasks = _todoFormState.value.tasks.toMutableList()
                tasks.add(TaskFormState())
                _todoFormState.value = _todoFormState.value.copy(tasks = tasks)
            }

            is AddEditTodoEvent.RemoveTask -> {
                if (todoFormState.value.tasks.size > 1) {
                    val tasks = _todoFormState.value.tasks.toMutableList()
                    tasks.removeAt(event.id)
                    _todoFormState.value = _todoFormState.value.copy(tasks = tasks)
                }
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
            preferences.collectLatest {
                getTodo(it.token, todoId)
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                _currentTodoId = result.data.id
                                _todoFormState.value = _todoFormState.value.copy(
                                    title = result.data.title,
                                    createdAt = result.data.createdAt,
                                    tasks = result.data.tasks?.map { task ->
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

                            is Resource.Error -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = false
                                )
                                if (result.message == INVALID_TOKEN) {
                                    sendUIEvent(
                                        UIEvent.Navigate(
                                            Routes.LOGIN
                                        )
                                    )
                                } else {
                                    sendUIEvent(
                                        UIEvent.ShowSnackbar(
                                            message = if (result.message.isNotEmpty()) {
                                                UIText.DynamicString(result.message)
                                            } else {
                                                UIText.StringResource(R.string.error)
                                            }
                                        )
                                    )
                                }

                            }

                            is Resource.Loading -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }.launchIn(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitData() {
        _todoFormState.value = todoFormState.value.copy(
            titleError = null,
            tasksEmptyError = null,
            tasks = todoFormState.value.tasks.map { it.copy(descriptionError = null) }
        )
        val titleResult = validateTodoTitle(_todoFormState.value.title)
        val taskEmpty = validateTaskEmpty(_todoFormState.value.tasks)
        val tasksDetailResult =
            _todoFormState.value.tasks.map { validateTaskDescription(it.description) }

        val hasError = listOf(
            titleResult,
            taskEmpty
        ).plus(tasksDetailResult)
            .any { !it.successful }
        if (hasError) {
            var i = 0
            _todoFormState.value = _todoFormState.value.copy(
                titleError = titleResult.errorMessage,
                tasksEmptyError = taskEmpty.errorMessage,
                tasks = _todoFormState.value.tasks.map { task ->
                    val copy = task.copy(descriptionError = tasksDetailResult[i].errorMessage)
                    i++
                    copy
                }
            )
            return
        }
        _currentTodoId?.let {
            updateTodo()
        } ?: addTodo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTodo() {
        val todo = TodoDTO(
            id = null,
            title = _todoFormState.value.title,
            createdAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            lastUpdatedAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            tasks = _todoFormState.value.tasks.map { task ->
                TaskDTO(
                    id = task.id,
                    description = task.description,
                    deadline = task.deadline?.let { DateFormatUtil.toISOInstantString(it) },
                    status = task.status,
                    todoId = null
                )
            }
        )
        viewModelScope.launch {
            preferences.collectLatest {
                addTodo(it.token, AddEditTodoRequestDTO(todo))
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                sendUIEvent(UIEvent.PopBackStack)
                            }

                            is Resource.Error -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = false
                                )
                                if (result.message == INVALID_TOKEN) {
                                    sendUIEvent(
                                        UIEvent.Navigate(
                                            Routes.LOGIN
                                        )
                                    )
                                } else {
                                    if (result.message.isNotEmpty()) {
                                        _apiError.value = UIText.DynamicString(result.message)
                                    } else {
                                        _apiError.value = UIText.StringResource(R.string.io_error)
                                    }
                                }
                            }

                            is Resource.Loading -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }.launchIn(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTodo() {
        val todo = TodoDTO(
            id = _currentTodoId,
            title = _todoFormState.value.title,
            createdAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            lastUpdatedAt = DateFormatUtil.toISOInstantString(DateFormatUtil.fromLong(System.currentTimeMillis())),
            tasks = _todoFormState.value.tasks.map { task ->
                TaskDTO(
                    id = task.id,
                    description = task.description,
                    deadline = task.deadline?.let { DateFormatUtil.toISOInstantString(it) },
                    status = task.status,
                    todoId = _currentTodoId
                )
            }
        )
        viewModelScope.launch {
            preferences.collectLatest {
                updateTodo(it.token, AddEditTodoRequestDTO(todo))
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                sendUIEvent(UIEvent.PopBackStack)
                            }

                            is Resource.Error -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = false
                                )
                                if (result.message == INVALID_TOKEN) {
                                    sendUIEvent(
                                        UIEvent.Navigate(
                                            Routes.LOGIN
                                        )
                                    )
                                } else {
                                    if (result.message.isNotEmpty()) {
                                        _apiError.value = UIText.DynamicString(result.message)
                                    } else {
                                        _apiError.value = UIText.StringResource(R.string.io_error)
                                    }
                                }
                            }

                            is Resource.Loading -> {
                                _todoFormState.value = _todoFormState.value.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }.launchIn(this)
            }
        }
    }

    private fun deleteTodo() {
        _currentTodoId?.let { todoId ->
            viewModelScope.launch {
                preferences.collectLatest {
                    deleteTodo(it.token, todoId)
                        .onEach { result ->
                            when (result) {
                                is Resource.Success -> {
                                    sendUIEvent(UIEvent.PopBackStack)
                                }

                                is Resource.Error -> {
                                    println("resource is error")
                                    _todoFormState.value = _todoFormState.value.copy(
                                        isLoading = false
                                    )
                                    if (result.message == INVALID_TOKEN) {
                                        sendUIEvent(
                                            UIEvent.Navigate(
                                                Routes.LOGIN
                                            )
                                        )
                                    } else {
                                        if (result.message.isNotEmpty()) {
                                            println("Message is not empty(${result.message})")
                                            _apiError.value = UIText.DynamicString(result.message)
                                        } else {
                                            println("Message is empty")
                                            _apiError.value = UIText.StringResource(R.string.io_error)
                                        }
                                    }
                                }

                                is Resource.Loading -> {
                                    _todoFormState.value = _todoFormState.value.copy(
                                        isLoading = true
                                    )
                                }
                            }
                        }.launchIn(this)
                }
            }
        }
    }
}