package ch.kra.todo.todo.presentation.todos

sealed class TodoListEvent {
    data class AddEditTodo(val todoId: Int): TodoListEvent()
    object Disconnect: TodoListEvent()
}
