package ch.kra.todo.core

import org.junit.Assert.*
import org.junit.Test

class DateFormatUtilTest {

    @Test
    fun testGetDateFormat() {
        val year = 2022
        val month = 4
        val day = 2
        val hour = 14
        val minutes = 50
        val seconds = 23
        val res = DateFormatUtil.fromDateTimeValues(year, month, day, hour, minutes, seconds)
        assertEquals("Day doesn't match", res.dayOfMonth, day)
        assertEquals("Month doesn't match", res.monthValue, month)
        assertEquals("Year doesn't match", res.year, year)
        assertEquals("Hour doesn't match", res.hour, hour)
        assertEquals("Minute doesn't match", res.minute, minutes)
        assertEquals("Second doesn't match", res.second, seconds)

        val day2 = 29
        val res2 = DateFormatUtil.fromDateTimeValues(year, month, day2, hour, minutes, seconds)
        assertEquals("Day doesn't match", res2.dayOfMonth, day2)
        assertEquals("Month doesn't match", res2.monthValue, month)
        assertEquals("Year doesn't match", res2.year, year)
        assertEquals("Hour doesn't match", res2.hour, hour)
        assertEquals("Minute doesn't match", res2.minute, minutes)
        assertEquals("Second doesn't match", res2.second, seconds)

        val month2 = 6
        val res3 = DateFormatUtil.fromDateTimeValues(year, month2, day2, hour, minutes, seconds)
        assertEquals("Day doesn't match", res3.dayOfMonth, day2)
        assertEquals("Month doesn't match", res3.monthValue, month2)
        assertEquals("Year doesn't match", res3.year, year)
        assertEquals("Hour doesn't match", res3.hour, hour)
        assertEquals("Minute doesn't match", res3.minute, minutes)
        assertEquals("Second doesn't match", res3.second, seconds)
    }

    @Test
    fun testGetDateFormatWithoutTime() {
        val year = 2022
        val month = 4
        val day = 2
        val res = DateFormatUtil.fromDateTimeValues(year, month, day)
        assertEquals("Day doesn't match", res.dayOfMonth, day)
        assertEquals("Month doesn't match", res.monthValue, month)
        assertEquals("Year doesn't match", res.year, year)
        assertEquals("Hour doesn't match", res.hour, 0)
        assertEquals("Minute doesn't match", res.minute, 0)
        assertEquals("Second doesn't match", res.second, 0)

        val day2 = 29
        val res2 = DateFormatUtil.fromDateTimeValues(year, month, day2)
        assertEquals("Day doesn't match", res2.dayOfMonth, day2)
        assertEquals("Month doesn't match", res2.monthValue, month)
        assertEquals("Year doesn't match", res2.year, year)
        assertEquals("Hour doesn't match", res2.hour, 0)
        assertEquals("Minute doesn't match", res2.minute, 0)
        assertEquals("Second doesn't match", res2.second, 0)

        val month2 = 6
        val res3 = DateFormatUtil.fromDateTimeValues(year, month2, day2)
        assertEquals("Day doesn't match", res3.dayOfMonth, day2)
        assertEquals("Month doesn't match", res3.monthValue, month2)
        assertEquals("Year doesn't match", res3.year, year)
        assertEquals("Hour doesn't match", res3.hour, 0)
        assertEquals("Minute doesn't match", res3.minute, 0)
        assertEquals("Second doesn't match", res3.second, 0)
    }

    @Test
    fun testDateStringFromLocalDateTime() {
        val expectedString = "29.04.2022"
        val year = 2022
        val month = 4
        val day = 29
        val hour = 10
        val minutes = 50
        val seconds = 50
        val dateTime = DateFormatUtil.fromDateTimeValues(year, month, day, hour, minutes, seconds)
        val formattedString = DateFormatUtil.formatStringDateFromLocalDateTime(dateTime)
        assertEquals("Doesn't match: $formattedString", formattedString, expectedString)
    }

    @Test
    fun testToISOInstantString() {
        val dateTime = "2022-04-12T08:05:58Z"
        val localDateTime = DateFormatUtil.fromISOInstantString(dateTime)
        val resString = DateFormatUtil.toISOInstantString(localDateTime)
        assertEquals("Doesn't match: $resString", resString, dateTime)
    }

    @Test
    fun testFromCurrentTimeMillis() {
        val longDate = 1651474402231
        val isoString = "2022-05-02T06:53:22.231Z"
        val localDateTime = DateFormatUtil.fromLong(longDate)
        val resString = DateFormatUtil.toISOInstantString(localDateTime)
        assertEquals("Doesn't match:", resString, isoString)
    }
}