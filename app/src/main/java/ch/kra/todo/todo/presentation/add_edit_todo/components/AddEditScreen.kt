package ch.kra.todo.todo.presentation.add_edit_todo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import ch.kra.todo.core.presentation.ui.theme.BorderColor
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoEvent
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoViewModel
import ch.kra.todo.todo.presentation.add_edit_todo.TodoFormState
import ch.kra.todo.todo.presentation.todos.TodoListEvent
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TodoDetail(
    todoState: TodoFormState,
    currentTodoId: Int?,
    onEvent: (AddEditTodoEvent) -> Unit
) {
    TodoCard {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = todoState.title,
                onValueChange = { onEvent(AddEditTodoEvent.TitleChanged(it)) },
                label = { Text(text = stringResource(R.string.title)) },
                modifier = Modifier
                    .fillMaxWidth()
            )

            LazyColumn(
                contentPadding = PaddingValues(top = 10.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                items(todoState.tasks.size) { taskId ->

                    Divider(
                        color = BorderColor,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                            Checkbox(
                                checked = todoState.tasks[taskId].status,
                                onCheckedChange = {
                                    onEvent(
                                        AddEditTodoEvent.StatusChanged(
                                            taskId,
                                            it
                                        )
                                    )
                                }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                        ) {
                            OutlinedTextField(
                                value = todoState.tasks[taskId].description,
                                label = { Text(text = stringResource(R.string.description)) },
                                onValueChange = {
                                    onEvent(
                                        AddEditTodoEvent.DescriptionChanged(
                                            taskId,
                                            it
                                        )
                                    )
                                }
                            )
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = stringResource(R.string.deadline))
                                /* Todo: DatePicker */
                            }
                        }

                        FloatingActionButton(
                            onClick = { onEvent(AddEditTodoEvent.RemoveTask(taskId)) },
                            backgroundColor = MaterialTheme.colors.primary,
                            modifier = Modifier.size(25.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_remove),
                                contentDescription = stringResource(R.string.remove_task),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Divider(
                color = BorderColor,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            FloatingActionButton(
                onClick = { onEvent(AddEditTodoEvent.AddTask) },
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { onEvent(AddEditTodoEvent.Save) },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                    )
                }

                currentTodoId?.let {
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                    )
                    Button(
                        onClick = { onEvent(AddEditTodoEvent.Delete) },
                        modifier = Modifier
                            .weight(1f)
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
}