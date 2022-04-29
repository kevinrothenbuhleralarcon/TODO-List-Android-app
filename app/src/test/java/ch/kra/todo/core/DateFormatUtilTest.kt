package ch.kra.todo.core

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class DateFormatUtilTest {

    @Test
    fun testGetDateFormat() {
        val year = 2022
        val month = 10
        val day = 30
        val hour = 20
        val minutes = 30
        val seconds = 50
        val res = DateFormatUtil.fromDateTimeValues(year, month, day, hour, minutes, seconds)
        assert(res.dayOfMonth == day)
        assert(res.monthValue == month)
        assert(res.year == year)
        assert(res.hour == hour)
        assert(res.minute == minutes)
        assert(res.second == seconds)
    }

    @Test
    fun testDateStringFromLocalDateTime() {
        val expectedString = "29.04.2022"
        val year = 2022
        val month = 4
        val day = 29
        val hour = 0
        val minutes = 0
        val seconds = 0
        val dateTime = DateFormatUtil.fromDateTimeValues(year, month, day, hour, minutes, seconds)
        val formatedString = DateFormatUtil.formatStringDateFromLocalDateTime(dateTime)
        assert(formatedString == expectedString)
    }
}