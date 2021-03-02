package com.example.progresscountdowntimer.calendar.format;


import androidx.annotation.NonNull;

import com.example.progresscountdowntimer.calendar.CalendarDay;
import com.example.progresscountdowntimer.calendar.bpcalendar.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.Locale;


/**
 * Format using a {@linkplain DateFormat} instance.
 */
public class DateFormatDayFormatter implements DayFormatter {

  private final DateTimeFormatter dateFormat;

  /**
   * Format using a default format
   */
  public DateFormatDayFormatter() {
    this(DateTimeFormatter.ofPattern(DEFAULT_FORMAT, Locale.getDefault()));
  }

  /**
   * Format using a specific {@linkplain DateFormat}
   *
   * @param format the format to use
   */
  public DateFormatDayFormatter(@NonNull final DateTimeFormatter format) {
    this.dateFormat = format;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull public String format(@NonNull final CalendarDay day) {
    return dateFormat.format(day.getDate());
  }
}
