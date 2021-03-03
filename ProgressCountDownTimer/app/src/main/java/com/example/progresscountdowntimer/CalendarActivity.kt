package com.example.progresscountdowntimer

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.progresscountdowntimer.calendar.CalendarDay
import com.example.progresscountdowntimer.calendar.MaterialCalendarView
import com.example.progresscountdowntimer.calendar.OnDateSelectedListener
import com.example.progresscountdowntimer.calendar.bpcalendar.LocalDate
import kotlin.math.roundToInt


class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val changeSelectedDay = findViewById<TextView>(R.id.editTextTextPersonName3)
        val changeSelectedMonth = findViewById<TextView>(R.id.editTextTextPersonName4)
        val changeSelectedYear = findViewById<TextView>(R.id.editTextTextPersonName5)
        val changeSelected = findViewById<Button>(R.id.button)
        val changeMonth = findViewById<TextView>(R.id.editTextTextPersonName)
        val changeYear = findViewById<TextView>(R.id.editTextTextPersonName2)
        val currentDate = findViewById<TextView>(R.id.current_date)
        val changeDateBtn = findViewById<Button>(R.id.button2)
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)



        calendarView.setOnDateChangedListener(object : OnDateSelectedListener{
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                currentDate.text = date.toString()
            }

        })
        currentDate.text = calendarView.currentDate.toString()
        changeDateBtn.setOnClickListener {
            calendarView.setCurrentDate(changeMonth.text.toString(),changeYear.text.toString())
        }

        changeSelected.setOnClickListener {
            val day = changeSelectedDay.text.toString().toInt()
            val month = changeSelectedMonth.text.toString().toInt()
            val year = changeSelectedYear.text.toString().toInt()
            calendarView.setSelectedDate(LocalDate.of(year, month, day),true)
        }


        val type = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        val color = ContextCompat.getColor(this, R.color.colorButton)
        calendarView.setWeekTitleTextColor(color,type, 14)
        calendarView.selectionColor = color
        calendarView.setHeaderTextStyle(24,type,color)
    }
}