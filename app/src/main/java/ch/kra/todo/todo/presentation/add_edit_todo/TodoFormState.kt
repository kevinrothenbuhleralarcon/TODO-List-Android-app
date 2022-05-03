package ch.kra.todo.todo.presentation.add_edit_todo

import ch.kra.todo.core.UIText
import java.time.LocalDateTime

data class TodoFormState(
    val title: String = "",
    val titleError: UIText? = null,
    val createdAt: LocalDateTime? = null,
    val tasks: List<TaskFormState> = listOf(TaskFormState()),
    val tasksEmptyError: UIText? = null,
    val isLoading: Boolean = false
)

data class TaskFormState(
    val id: Int? = null,
    val description: String = "",
    val descriptionError: UIText? = null,
    val status: Boolean = false,
    val deadline: LocalDateTime? = null
)
