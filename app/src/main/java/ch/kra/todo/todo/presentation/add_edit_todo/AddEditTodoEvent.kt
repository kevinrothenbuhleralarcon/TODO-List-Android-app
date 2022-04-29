package ch.kra.todo.todo.presentation.add_edit_todo

import java.time.LocalDateTime

sealed class AddEditTodoEvent {
    object Save: AddEditTodoEvent()
    object Delete: AddEditTodoEvent()
    object NavigateBack: AddEditTodoEvent()
    data class TitleChanged(val value: String): AddEditTodoEvent()
    data class DescriptionChanged(val id: Int, val value: String): AddEditTodoEvent()
    data class StatusChanged(val id: Int, val value: Boolean): AddEditTodoEvent()
    data class DeadlineChanged(val id: Int, val value: LocalDateTime): AddEditTodoEvent()
    object AddTask: AddEditTodoEvent()
    data class RemoveTask(val id: Int): AddEditTodoEvent()
}
