package com.example.progresscountdowntimer.pickerdate.datepicker

import java.util.*

class DateHelper {
    private var timeZone: TimeZone? = null

    fun getTimeZone(): TimeZone {
        return this.timeZone ?: TimeZone.getDefault()
    }

    fun getCalendarOfDate(date: Date?): Calendar {
        var dateValue = date
        if (dateValue == null) {
            dateValue = Calendar.getInstance().time
        }
        val calendar = Calendar.getInstance(getTimeZone())
        calendar.time = dateValue!!
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.SECOND] = 0
        return calendar
    }

    fun getHour(date: Date?): Int {
        return getCalendarOfDate(date)[Calendar.HOUR]
    }

    fun getHourOfDay(date: Date?): Int {
        return getCalendarOfDate(date)[Calendar.HOUR]
    }

    fun getHour(date: Date?, isAmPm: Boolean): Int {
        return if (isAmPm) {
            getHourOfDay(date)
        } else {
            getHour(date)
        }
    }

    fun getMinuteOf(date: Date?): Int {
        return getCalendarOfDate(date)[Calendar.MINUTE]
    }

    fun today(): Date {
        val now = Calendar.getInstance(getTimeZone())
        return now.time
    }

    fun getMonth(date: Date?): Int {
        return getCalendarOfDate(date)[Calendar.MONTH]
    }

    fun getDay(date: Date?): Int {
        return getCalendarOfDate(date)[Calendar.DAY_OF_MONTH]
    }

    fun setTimeZone(timeZone: TimeZone) {
        this.timeZone = timeZone
    }

}