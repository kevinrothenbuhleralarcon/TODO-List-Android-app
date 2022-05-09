package ch.kra.todo.todo.presentation.todos

sealed class TodoListEvent {
    object AddTodo: TodoListEvent()
    data class EditTodo(val todoId: Int): TodoListEvent()
    object Disconnect: TodoListEvent()
    object Refresh: TodoListEvent()
}
