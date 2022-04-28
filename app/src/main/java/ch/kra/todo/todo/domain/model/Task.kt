package ch.kra.todo.todo.domain.model

import java.time.Instant
import java.time.LocalDateTime

data class Task(
    val id: Int? = null,
    val description: String,
    val status: Boolean,
    val deadline: LocalDateTime? = null,
    val todoId: Int? = null
)
