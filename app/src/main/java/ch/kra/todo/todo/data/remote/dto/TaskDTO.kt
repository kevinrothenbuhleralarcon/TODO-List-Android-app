package ch.kra.todo.todo.data.remote.dto

import android.os.Build
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.todo.domain.model.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

data class TaskDTO(
    val id: Int? = null,
    val description: String,
    val deadline: String? = null,
    val status: Boolean,
    val todoId: Int? = null
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toTask(): Task {
        var deadlineDate: LocalDateTime? = null
        deadline?.let {
            deadlineDate = try { DateFormatUtil.fromISOInstantString(it) } catch (err: DateTimeParseException) { null }
        }
        return Task(
            id = id,
            description = description,
            status = status,
            deadline = deadlineDate,
            todoId = todoId
        )
    }
}