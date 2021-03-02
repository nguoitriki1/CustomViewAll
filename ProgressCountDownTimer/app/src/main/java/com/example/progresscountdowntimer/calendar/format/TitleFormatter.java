package com.example.progresscountdowntimer.calendar.format;


import com.example.progresscountdowntimer.calendar.CalendarDay;

public interface TitleFormatter {

  String DEFAULT_FORMAT = "LLLL yyyy";

  TitleFormatter DEFAULT = new DateFormatTitleFormatter();

  /**
   * Converts the supplied day to a suitable month/year title
   *
   * @param day the day containing relevant month and year information
   * @return a label to display for the given month/year
   */
  CharSequence format(CalendarDay day);
}
