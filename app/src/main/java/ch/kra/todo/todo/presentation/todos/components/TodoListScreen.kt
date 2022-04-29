package ch.kra.todo.todo.presentation.todos.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.R
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.Footer
import ch.kra.todo.core.presentation.Header
import ch.kra.todo.core.presentation.LoadingWrapper
import ch.kra.todo.core.presentation.TodoCard
import ch.kra.todo.core.presentation.ui.theme.TextInfoColor
import ch.kra.todo.todo.domain.model.Todo
import ch.kra.todo.todo.presentation.todos.TodoListEvent
import ch.kra.todo.todo.presentation.todos.TodoListViewModel
import kotlinx.coroutines.flow.collect

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel(),
    navigate: (UIEvent.Navigate) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val username = viewModel.username.value
    val todoListState = viewModel.todoListState.value

    LaunchedEffect(key1 = true) {
        viewModel.getTodoList()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> {
                    navigate(event)
                }

                is UIEvent.ShowSnackbar -> {

                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TodoListEvent.AddTodo) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_todo))
            }
        },
        bottomBar = {
            Footer(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            )
        },
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth(),
                username = username
            )
            Text(
                modifier = Modifier
                    .padding(20.dp),
                text = stringResource(R.string.todo_list_header, username),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            LoadingWrapper(
                modifier = Modifier.fillMaxSize(),
                isLoading = todoListState.isLoading
            ) {
                TodoList(
                    todoList = todoListState.todoList,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TodoList(
    todoList: List<Todo>,
    onEvent: (TodoListEvent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(todoList.size) {
            TodoCard(
                modifier = Modifier
                    .clickable { onEvent(TodoListEvent.EditTodo(todoList[it].id ?: 0)) }
            ) {
                TodoItem(todo = todoList[it])
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TodoItem(
    todo: Todo
) {
    Column {
        Text(
            text = todo.title,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(
                R.string.created_on,
                DateFormatUtil.formatStringDateTimeFromLocalDateTime(todo.createdAt)
            ),
            color = TextInfoColor
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = stringResource(
                R.string.last_updated_on,
                DateFormatUtil.formatStringDateTimeFromLocalDateTime(todo.lastUpdatedAt)
            ),
            color = TextInfoColor
        )
    }
}