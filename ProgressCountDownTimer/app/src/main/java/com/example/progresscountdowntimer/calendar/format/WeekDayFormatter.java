package com.example.progresscountdowntimer.calendar.format;


import com.example.progresscountdowntimer.calendar.bpcalendar.DayOfWeek;

public interface WeekDayFormatter {
  CharSequence format(DayOfWeek dayOfWeek);

  WeekDayFormatter DEFAULT = new CalendarWeekDayFormatter();
}
