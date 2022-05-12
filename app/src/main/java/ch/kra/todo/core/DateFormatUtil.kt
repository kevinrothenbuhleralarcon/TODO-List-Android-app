package ch.kra.todo.core

import android.os.Build
import androidx.annotation.RequiresApi
import ch.kra.todo.core.Constants.DATE_PATTERN
import ch.kra.todo.core.Constants.DATE_TIME_PATTERN
import ch.kra.todo.core.Constants.ZONE_ID
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateFormatUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatStringDateTimeFromLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatStringDateFromLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromLong(date: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of(ZONE_ID));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromDateTimeValues(year: Int, month: Int, day: Int, hour: Int = 0, minutes: Int = 0, seconds: Int = 0): LocalDateTime {
        val dtf = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s")
        val stringDateTime = "$year-$month-$day $hour:$minutes:$seconds"
        return LocalDateTime.parse(stringDateTime, dtf)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromISOInstantString(dateTime: String): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.parse(dateTime), ZoneId.of(ZONE_ID))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toISOInstantString(dateTime: LocalDateTime): String {
        return dateTime.atZone(ZoneId.of(ZONE_ID)).toInstant().toString()
    }
}