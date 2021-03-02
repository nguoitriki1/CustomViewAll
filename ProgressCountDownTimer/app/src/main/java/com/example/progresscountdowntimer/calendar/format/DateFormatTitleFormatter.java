package com.example.progresscountdowntimer.calendar.format;


import com.example.progresscountdowntimer.calendar.CalendarDay;
import com.example.progresscountdowntimer.calendar.bpcalendar.format.DateTimeFormatter;


/**
 * Format using a {@linkplain java.text.DateFormat} instance.
 */
public class DateFormatTitleFormatter implements TitleFormatter {

  private final DateTimeFormatter dateFormat;


  public DateFormatTitleFormatter() {
    this(DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
  }

  /**
   * Format using a specified {@linkplain DateTimeFormatter}
   *
   * @param format the format to use
   */
  public DateFormatTitleFormatter(final DateTimeFormatter format) {
    this.dateFormat = format;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CharSequence format(final CalendarDay day) {
    return dateFormat.format(day.getDate());
  }
}
