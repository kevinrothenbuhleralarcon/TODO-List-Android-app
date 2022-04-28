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
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.todo.domain.use_case.GetTodoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    init {
        loadSettings()
        getTodoList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodoList() {
        viewModelScope.launch {
            getTodoList(_token.value)
                .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        
                    }

                    is Resource.Error -> {
                        Log.d("todo", "error: ${result.message}")
                    }

                    is Resource.Loading -> {

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