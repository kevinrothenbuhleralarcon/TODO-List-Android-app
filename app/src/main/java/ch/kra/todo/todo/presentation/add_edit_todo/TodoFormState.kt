package ch.kra.todo.todo.presentation.add_edit_todo

import java.time.LocalDateTime

data class TodoFormState(
    val title: String = "",
    val titleError: String? = null,
    val createdAt: LocalDateTime? = null,
    val tasks: List<TaskFormState> = emptyList(),
    val isLoading: Boolean = false
)

data class TaskFormState(
    val id: Int? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val status: Boolean = false,
    val deadline: LocalDateTime? = null
)
