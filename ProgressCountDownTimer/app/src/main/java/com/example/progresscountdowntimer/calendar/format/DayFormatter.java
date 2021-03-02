package com.example.progresscountdowntimer.calendar.format;

import androidx.annotation.NonNull;

import com.example.progresscountdowntimer.calendar.CalendarDay;

import java.text.SimpleDateFormat;

/**
 * Supply labels for a given day. Default implementation is to format using a {@linkplain SimpleDateFormat}
 */
public interface DayFormatter {

  /**
   * Default format for displaying the day.
   */
  String DEFAULT_FORMAT = "d";

  DayFormatter DEFAULT = new DateFormatDayFormatter();

  /**
   * Format a given day into a string
   *
   * @param day the day
   * @return a label for the day
   */
  @NonNull
  String format(@NonNull CalendarDay day);
}
