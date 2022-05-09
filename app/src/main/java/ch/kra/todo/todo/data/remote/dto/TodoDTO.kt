package ch.kra.todo.todo.data.remote.dto

import android.os.Build
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants.ZONE_ID
import ch.kra.todo.core.DateFormatUtil
import ch.kra.todo.todo.domain.model.Todo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

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
            createdAt = try { DateFormatUtil.fromISOInstantString(createdAt) } catch (err: DateTimeParseException) { DateFormatUtil.fromLong(System.currentTimeMillis()) },
            lastUpdatedAt = try { DateFormatUtil.fromISOInstantString(lastUpdatedAt) } catch (err: DateTimeParseException) { DateFormatUtil.fromLong(System.currentTimeMillis()) },
            tasks = tasks?.map { it.toTask() }
        )
    }
}