package ch.kra.todo.todo.presentation.add_edit_todo.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.kra.todo.R
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.core.UIEvent
import ch.kra.todo.core.presentation.ui.shared_composable.Footer
import ch.kra.todo.core.presentation.ui.shared_composable.Header
import ch.kra.todo.core.presentation.ui.shared_composable.LoadingWrapper
import ch.kra.todo.core.presentation.ui.shared_composable.TodoCard
import ch.kra.todo.core.presentation.ui.shared_composable.DatePicker
import ch.kra.todo.core.presentation.ui.theme.BorderColor
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoEvent
import ch.kra.todo.todo.presentation.add_edit_todo.AddEditTodoViewModel
import kotlinx.coroutines.flow.collect

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditScreen(
    viewModel: AddEditTodoViewModel = hiltViewModel(),
    navigate: (UIEvent.Navigate) -> Unit,
    popBackStack: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val username = viewModel.username.value
    val currentTodoId = viewModel.currentTodoId
    val todoState = viewModel.todoFormState.value

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.Navigate -> {
                    navigate(event)
                }

                is UIEvent.PopBackStack -> {
                    popBackStack()
                }

                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }

                else -> {}
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
                    currentTodoId = currentTodoId,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TodoDetail(
    viewModel: AddEditTodoViewModel = hiltViewModel(),
    currentTodoId: Int?,
    onEvent: (AddEditTodoEvent) -> Unit
) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    val todoState = viewModel.todoFormState.value
    val apiError = viewModel.apiError.value

    TodoCard {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
        ) {

            if(apiError.asString().isNotEmpty()) {
                Text(
                    text = apiError.asString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = todoState.title,
                onValueChange = { onEvent(AddEditTodoEvent.TitleChanged(it)) },
                label = { Text(text = stringResource(R.string.title)) },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = todoState.titleError != null
            )
            if (todoState.titleError != null) {
                Text(
                    text = todoState.titleError.asString(),
                    color = MaterialTheme.colors.error
                )
            }

            if(todoState.tasksEmptyError != null) {
                Text(
                    text = todoState.tasksEmptyError.asString(),
                    color = MaterialTheme.colors.error
                )
            }
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
                                singleLine = true,
                                onValueChange = {
                                    onEvent(
                                        AddEditTodoEvent.DescriptionChanged(
                                            taskId,
                                            it
                                        )
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    textDecoration = if (todoState.tasks[taskId].status) {
                                        TextDecoration.LineThrough
                                    } else {
                                        TextDecoration.None
                                    }
                                ),
                                isError = todoState.tasks[taskId].descriptionError != null
                            )
                            if (todoState.tasks[taskId].descriptionError != null) {
                                Text(
                                    text = todoState.tasks[taskId].descriptionError!!.asString(),
                                    color = MaterialTheme.colors.error
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                DatePicker(
                                    date = todoState.tasks[taskId].deadline?.let { DateFormatUtil.formatStringDateFromLocalDateTime(it) },
                                    onUpdateDate = {
                                        it?.let {
                                            onEvent(AddEditTodoEvent.DeadlineChanged(taskId, it))
                                        }
                                    }
                                )
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
                        onClick = { openDialog = true },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                        )
                    }
                    
                    if (openDialog) {
                        AlertDialog(
                            onDismissRequest = {  },
                            title = { Text(text = stringResource(R.string.confirmation)) },
                            text = { Text(text = stringResource(R.string.delete_dialog_text)) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        openDialog = false
                                        onEvent(AddEditTodoEvent.Delete)
                                    }
                                ) {
                                    Text(text = stringResource(R.string.yes))
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = stringResource(R.string.no))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}