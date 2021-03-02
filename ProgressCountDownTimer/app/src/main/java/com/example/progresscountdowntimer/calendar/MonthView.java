package com.example.progresscountdowntimer.calendar;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.progresscountdowntimer.calendar.bpcalendar.DayOfWeek;
import com.example.progresscountdowntimer.calendar.bpcalendar.LocalDate;

import java.util.Collection;


@SuppressLint("ViewConstructor") class MonthView extends CalendarPagerView {

  public MonthView(
      @NonNull final MaterialCalendarView view,
      final CalendarDay month,
      final DayOfWeek firstDayOfWeek,
      final boolean showWeekDays) {
    super(view, month, firstDayOfWeek, showWeekDays);
  }

  @Override
  protected void buildDayViews(
      final Collection<DayView> dayViews,
      final LocalDate calendar) {
    LocalDate temp = calendar;
    for (int r = 0; r < DEFAULT_MAX_WEEKS; r++) {
      for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
        addDayView(dayViews, temp);
        temp = temp.plusDays(1);
      }
    }
  }

  public CalendarDay getMonth() {
    return getFirstViewDay();
  }

  @Override
  protected boolean isDayEnabled(final CalendarDay day) {
    return day.getMonth() == getFirstViewDay().getMonth();
  }

  @Override
  protected int getRows() {
    return showWeekDays ? DEFAULT_MAX_WEEKS + DAY_NAMES_ROW : DEFAULT_MAX_WEEKS;
  }
}
