package com.example.progresscountdowntimer.calendar.format;

import com.example.progresscountdowntimer.calendar.bpcalendar.DayOfWeek;
import com.example.progresscountdowntimer.calendar.bpcalendar.format.TextStyle;

import java.util.Locale;

public final class CalendarWeekDayFormatter implements WeekDayFormatter {
  /**
   * Format the day of the week with using {@link TextStyle#SHORT} by default.
   *
   * @see java.time.DayOfWeek#getDisplayName(java.time.format.TextStyle, Locale)
   */
  @Override
  public CharSequence format(final DayOfWeek dayOfWeek) {
    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
  }
}
