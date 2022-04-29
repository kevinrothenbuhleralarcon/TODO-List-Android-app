package ch.kra.todo.todo.data.remote.dto

import android.os.Build
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants.ZONE_ID
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.todo.domain.model.Todo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class TodoDTO(
    val id: Int? = null,
    val title: String,
    val createdAt: String,
    val lastUpdatedAt: String,
    val tasks: List<TaskDTO>? = null
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toTodo(): Todo {
        return Todo(
            id = id,
            title = title,
            createdAt = DateFormatUtil.fromISOInstantString(createdAt),
            lastUpdatedAt = DateFormatUtil.fromISOInstantString(lastUpdatedAt),
            tasks = tasks?.map { it.toTask() }
        )
    }
}