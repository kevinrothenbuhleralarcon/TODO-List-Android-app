package ch.kra.todo.todo.domain.model

import java.time.LocalDateTime


data class Todo(
    val id: Int? = null,
    val title: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val tasks: List<Task>? = null
)
