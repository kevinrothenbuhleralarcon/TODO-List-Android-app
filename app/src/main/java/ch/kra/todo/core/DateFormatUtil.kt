package ch.kra.todo.core

import android.os.Build
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants.DATE_PATTERN
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateFormatUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToString(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }
}