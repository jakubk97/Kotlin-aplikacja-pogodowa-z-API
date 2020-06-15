package com.example.kuba.helpers

import java.text.SimpleDateFormat
import java.util.*

class DateAndTimeHelper {
    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.getTime())
    }

    fun getTime(milliSeconds: Long): String? {
        return String.format(
            " %d:%d",
            ((milliSeconds / (1000 * 60 * 60)) % 24),
            ((milliSeconds / (1000 * 60)) % 60)
        )
    }
}