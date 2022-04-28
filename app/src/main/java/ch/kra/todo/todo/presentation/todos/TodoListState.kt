package ch.kra.todo.todo.presentation.todos

import ch.kra.todo.todo.domain.model.Todo

data class TodoListState(
    val todoList: List<Todo> = emptyList(),
    val isLoading: Boolean = false
)