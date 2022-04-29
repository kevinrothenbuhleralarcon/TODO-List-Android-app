package ch.kra.todo.todo.presentation.add_edit_todo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.R
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.Footer
import ch.kra.todo.core.presentation.Header
import ch.kra.todo.core.presentation.LoadingWrapper
import ch.kra.todo.core.presentation.TodoCard
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoEvent
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoViewModel
import ch.kra.todo.todo.presentation.add_edit_todo.TodoFormState
import kotlinx.coroutines.flow.collect

@Composable
fun AddEditScreen(
    viewModel: AddEditTodoViewModel = hiltViewModel(),
    navigate: (UIEvent.Navigate) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val username = viewModel.username.value
    val currentTodoId = viewModel.currentTodoId
    val todoState = viewModel.state

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> {
                    navigate(event)
                }

                is UIEvent.PopBackStack -> {

                }

                is UIEvent.ShowSnackbar -> {

                }
            }
        }
    }

    Scaffold(
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
                text = todoState.title.ifEmpty { stringResource(R.string.new_todo) },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            LoadingWrapper(
                modifier = Modifier.fillMaxSize(),
                isLoading = todoState.isLoading
            ) {
                TodoDetail(
                    todoState = todoState,
                    currentTodoId = currentTodoId,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
private fun TodoDetail (
    todoState: TodoFormState,
    currentTodoId: Int?,
    onEvent: (AddEditTodoEvent) -> Unit
) {
    TodoCard {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = todoState.title,
                onValueChange = { onEvent(AddEditTodoEvent.TitleChanged(it)) },
                label = { Text(text = stringResource(R.string.title)) }
            )

            LazyColumn(
                contentPadding = PaddingValues(top = 10.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                items(todoState.tasks.size) { taskId ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = todoState.tasks[taskId].status,
                            onCheckedChange = { onEvent(AddEditTodoEvent.StatusChanged(taskId, it)) }
                        )

                        Column(
                            modifier = Modifier
                        ) {
                            OutlinedTextField(
                                value = todoState.tasks[taskId].description,
                                onValueChange = { onEvent(AddEditTodoEvent.DescriptionChanged(taskId, it)) }
                            )
                            Row(
                                modifier = Modifier
                            ) {
                                Text(text = stringResource(R.string.deadline))
                                /* Todo: DatePicker */
                            }
                        }

                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .background(MaterialTheme.colors.primary)
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_remove), contentDescription = "Remove")
                        }
                    }
                }
            }

            Button(
                onClick = { onEvent(AddEditTodoEvent.Save) },
            ) {
                Text(
                    text = stringResource(R.string.save),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                )
            }

            currentTodoId?.let {
                Button(
                    onClick = { onEvent(AddEditTodoEvent.Delete) }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}