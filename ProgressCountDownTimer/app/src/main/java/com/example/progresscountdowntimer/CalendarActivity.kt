package com.example.progresscountdowntimer

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.progresscountdowntimer.calendar.CalendarDay
import com.example.progresscountdowntimer.calendar.MaterialCalendarView
import com.example.progresscountdowntimer.calendar.bpcalendar.LocalDate
import kotlin.math.roundToInt


class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
        calendarView.currentDate = CalendarDay.from(1990, 10, 19)
        calendarView.setSelectedDate(LocalDate.of(1990, 10, 19))
        val type = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        val color = ContextCompat.getColor(this, R.color.colorButton)
        calendarView.setWeekTitleTextColor(color,type, 14)
        calendarView.selectionColor = color
        calendarView.setHeaderTextStyle(24,type,color)
    }
}