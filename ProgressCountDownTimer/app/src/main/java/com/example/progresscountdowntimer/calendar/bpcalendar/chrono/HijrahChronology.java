/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.progresscountdowntimer.calendar.bpcalendar.chrono;


import com.example.progresscountdowntimer.calendar.bpcalendar.Clock;
import com.example.progresscountdowntimer.calendar.bpcalendar.DateTimeException;
import com.example.progresscountdowntimer.calendar.bpcalendar.DayOfWeek;
import com.example.progresscountdowntimer.calendar.bpcalendar.Instant;
import com.example.progresscountdowntimer.calendar.bpcalendar.LocalDate;
import com.example.progresscountdowntimer.calendar.bpcalendar.ZoneId;
import com.example.progresscountdowntimer.calendar.bpcalendar.format.ResolverStyle;
import com.example.progresscountdowntimer.calendar.bpcalendar.jdk8.Jdk8Methods;
import com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField;
import com.example.progresscountdowntimer.calendar.bpcalendar.temporal.TemporalAccessor;
import com.example.progresscountdowntimer.calendar.bpcalendar.temporal.TemporalField;
import com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ValueRange;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.DAY_OF_MONTH;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.DAY_OF_WEEK;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.DAY_OF_YEAR;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.EPOCH_DAY;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.ERA;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.MONTH_OF_YEAR;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.PROLEPTIC_MONTH;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.YEAR;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoField.YEAR_OF_ERA;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoUnit.DAYS;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoUnit.MONTHS;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.ChronoUnit.WEEKS;
import static com.example.progresscountdowntimer.calendar.bpcalendar.temporal.TemporalAdjusters.nextOrSame;

public final class HijrahChronology extends Chronology implements Serializable {

    /**
     * Singleton instance of the Hijrah chronology.
     */
    public static final    HijrahChronology INSTANCE = new    HijrahChronology();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3127340209035924785L;
    /**
     * Narrow names for eras.
     */
    private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap<String, String[]>();
    /**
     * Short names for eras.
     */
    private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap<String, String[]>();
    /**
     * Full names for eras.
     */
    private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap<String, String[]>();
    /**
     * Fallback language for the era names.
     */
    private static final String FALLBACK_LANGUAGE = "en";

    /**
     * Language that has the era names.
     */
    //private static final String TARGET_LANGUAGE = "ar";
    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BH", "HE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.H.", "H.E."});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Hijrah", "Hijrah Era"});
    }

    /**
     * Restrictive constructor.
     */
    private HijrahChronology() {
    }

    /**
     * Resolve singleton.
     *
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology - 'Hijrah-umalqura'.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Hijrah-umalqura'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Hijrah-umalqura";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'islamic-umalqura'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'islamic-umalqura'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "islamic-umalqura";
    }

    //-----------------------------------------------------------------------
    @Override  // override with covariant return type
    public    HijrahDate date(   Era era, int yearOfEra, int month, int dayOfMonth) {
        return (   HijrahDate) super.date(era, yearOfEra, month, dayOfMonth);
    }

    @Override  // override with covariant return type
    public    HijrahDate date(int prolepticYear, int month, int dayOfMonth) {
        return    HijrahDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override  // override with covariant return type
    public    HijrahDate dateYearDay(   Era era, int yearOfEra, int dayOfYear) {
        return (   HijrahDate) super.dateYearDay(era, yearOfEra, dayOfYear);
    }

    @Override  // override with covariant return type
    public    HijrahDate dateYearDay(int prolepticYear, int dayOfYear) {
        return    HijrahDate.of(prolepticYear, 1, 1).plusDays(dayOfYear - 1);  // TODO better
    }

    @Override
    public    HijrahDate dateEpochDay(long epochDay) {
        return    HijrahDate.of(LocalDate.ofEpochDay(epochDay));
    }

    //-----------------------------------------------------------------------
    @Override  // override with covariant return type
    public    HijrahDate date(TemporalAccessor temporal) {
        if (temporal instanceof    HijrahDate) {
            return (   HijrahDate) temporal;
        }
        return    HijrahDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
    }

    @SuppressWarnings("unchecked")
    @Override  // override with covariant return type
    public ChronoLocalDateTime<   HijrahDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<   HijrahDate>) super.localDateTime(temporal);
    }

    @SuppressWarnings("unchecked")
    @Override  // override with covariant return type
    public    ChronoZonedDateTime<   HijrahDate> zonedDateTime(TemporalAccessor temporal) {
        return (   ChronoZonedDateTime<   HijrahDate>) super.zonedDateTime(temporal);
    }

    @SuppressWarnings("unchecked")
    @Override  // override with covariant return type
    public    ChronoZonedDateTime<   HijrahDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (   ChronoZonedDateTime<   HijrahDate>) super.zonedDateTime(instant, zone);
    }

    //-----------------------------------------------------------------------
    @Override  // override with covariant return type
    public    HijrahDate dateNow() {
        return (   HijrahDate) super.dateNow();
    }

    @Override  // override with covariant return type
    public    HijrahDate dateNow(ZoneId zone) {
        return (   HijrahDate) super.dateNow(zone);
    }

    @Override  // override with covariant return type
    public    HijrahDate dateNow(Clock clock) {
        Jdk8Methods.requireNonNull(clock, "clock");
        return (   HijrahDate) super.dateNow(clock);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return    HijrahDate.isLeapYear(prolepticYear);
    }

    @Override
    public int prolepticYear(   Era era, int yearOfEra) {
        if (era instanceof    HijrahEra == false) {
            throw new ClassCastException("Era must be HijrahEra");
        }
        return (era ==    HijrahEra.AH ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public    HijrahEra eraOf(int eraValue) {
        switch (eraValue) {
            case 0:
                return    HijrahEra.BEFORE_AH;
            case 1:
                return    HijrahEra.AH;
            default:
                throw new DateTimeException("invalid Hijrah era");
        }
    }

    @Override
    public List<   Era> eras() {
        return Arrays.<   Era>asList(   HijrahEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(ChronoField field) {
        return field.range();
    }

    @Override
    public    HijrahDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        if (fieldValues.containsKey(EPOCH_DAY)) {
            return dateEpochDay(fieldValues.remove(EPOCH_DAY));
        }

        // normalize fields
        Long prolepticMonth = fieldValues.remove(PROLEPTIC_MONTH);
        if (prolepticMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                PROLEPTIC_MONTH.checkValidValue(prolepticMonth);
            }
            updateResolveMap(fieldValues, MONTH_OF_YEAR, Jdk8Methods.floorMod(prolepticMonth, 12) + 1);
            updateResolveMap(fieldValues, YEAR, Jdk8Methods.floorDiv(prolepticMonth, 12));
        }

        // eras
        Long yoeLong = fieldValues.remove(YEAR_OF_ERA);
        if (yoeLong != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                YEAR_OF_ERA.checkValidValue(yoeLong);
            }
            Long era = fieldValues.remove(ERA);
            if (era == null) {
                Long year = fieldValues.get(YEAR);
                if (resolverStyle == ResolverStyle.STRICT) {
                    // do not invent era if strict, but do cross-check with year
                    if (year != null) {
                        updateResolveMap(fieldValues, YEAR, (year > 0 ? yoeLong: Jdk8Methods.safeSubtract(1, yoeLong)));
                    } else {
                        // reinstate the field removed earlier, no cross-check issues
                        fieldValues.put(YEAR_OF_ERA, yoeLong);
                    }
                } else {
                    // invent era
                    updateResolveMap(fieldValues, YEAR, (year == null || year > 0 ? yoeLong: Jdk8Methods.safeSubtract(1, yoeLong)));
                }
            } else if (era.longValue() == 1L) {
                updateResolveMap(fieldValues, YEAR, yoeLong);
            } else if (era.longValue() == 0L) {
                updateResolveMap(fieldValues, YEAR, Jdk8Methods.safeSubtract(1, yoeLong));
            } else {
                throw new DateTimeException("Invalid value for era: " + era);
            }
        } else if (fieldValues.containsKey(ERA)) {
            ERA.checkValidValue(fieldValues.get(ERA));  // always validated
        }

        // build date
        if (fieldValues.containsKey(YEAR)) {
            if (fieldValues.containsKey(MONTH_OF_YEAR)) {
                if (fieldValues.containsKey(DAY_OF_MONTH)) {
                    int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                    if (resolverStyle == ResolverStyle.LENIENT) {
                        long months = Jdk8Methods.safeSubtract(fieldValues.remove(MONTH_OF_YEAR), 1);
                        long days = Jdk8Methods.safeSubtract(fieldValues.remove(DAY_OF_MONTH), 1);
                        return date(y, 1, 1).plusMonths(months).plusDays(days);
                    } else {
                        int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
                        int dom = range(DAY_OF_MONTH).checkValidIntValue(fieldValues.remove(DAY_OF_MONTH), DAY_OF_MONTH);
                        if (resolverStyle == ResolverStyle.SMART && dom > 28) {
                            dom = Math.min(dom, date(y, moy, 1).lengthOfMonth());
                        }
                        return date(y, moy, dom);
                    }
                }
                if (fieldValues.containsKey(ALIGNED_WEEK_OF_MONTH)) {
                    if (fieldValues.containsKey(ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                        if (resolverStyle == ResolverStyle.LENIENT) {
                            long months = Jdk8Methods.safeSubtract(fieldValues.remove(MONTH_OF_YEAR), 1);
                            long weeks = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
                            long days = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH), 1);
                            return date(y, 1, 1).plus(months, MONTHS).plus(weeks, WEEKS).plus(days, DAYS);
                        }
                        int moy = MONTH_OF_YEAR.checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR));
                        int aw = ALIGNED_WEEK_OF_MONTH.checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH));
                        int ad = ALIGNED_DAY_OF_WEEK_IN_MONTH.checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                           HijrahDate date = date(y, moy, 1).plus((aw - 1) * 7 + (ad - 1), DAYS);
                        if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) {
                            throw new DateTimeException("Strict mode rejected date parsed to a different month");
                        }
                        return date;
                    }
                    if (fieldValues.containsKey(DAY_OF_WEEK)) {
                        int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                        if (resolverStyle == ResolverStyle.LENIENT) {
                            long months = Jdk8Methods.safeSubtract(fieldValues.remove(MONTH_OF_YEAR), 1);
                            long weeks = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
                            long days = Jdk8Methods.safeSubtract(fieldValues.remove(DAY_OF_WEEK), 1);
                            return date(y, 1, 1).plus(months, MONTHS).plus(weeks, WEEKS).plus(days, DAYS);
                        }
                        int moy = MONTH_OF_YEAR.checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR));
                        int aw = ALIGNED_WEEK_OF_MONTH.checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH));
                        int dow = DAY_OF_WEEK.checkValidIntValue(fieldValues.remove(DAY_OF_WEEK));
                           HijrahDate date = date(y, moy, 1).plus(aw - 1, WEEKS).with(nextOrSame(DayOfWeek.of(dow)));
                        if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) {
                            throw new DateTimeException("Strict mode rejected date parsed to a different month");
                        }
                        return date;
                    }
                }
            }
            if (fieldValues.containsKey(DAY_OF_YEAR)) {
                int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                if (resolverStyle == ResolverStyle.LENIENT) {
                    long days = Jdk8Methods.safeSubtract(fieldValues.remove(DAY_OF_YEAR), 1);
                    return dateYearDay(y, 1).plusDays(days);
                }
                int doy = DAY_OF_YEAR.checkValidIntValue(fieldValues.remove(DAY_OF_YEAR));
                return dateYearDay(y, doy);
            }
            if (fieldValues.containsKey(ALIGNED_WEEK_OF_YEAR)) {
                if (fieldValues.containsKey(ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                    if (resolverStyle == ResolverStyle.LENIENT) {
                        long weeks = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
                        long days = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR), 1);
                        return date(y, 1, 1).plus(weeks, WEEKS).plus(days, DAYS);
                    }
                    int aw = ALIGNED_WEEK_OF_YEAR.checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR));
                    int ad = ALIGNED_DAY_OF_WEEK_IN_YEAR.checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                       HijrahDate date = date(y, 1, 1).plusDays((aw - 1) * 7 + (ad - 1));
                    if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
                        throw new DateTimeException("Strict mode rejected date parsed to a different year");
                    }
                    return date;
                }
                if (fieldValues.containsKey(DAY_OF_WEEK)) {
                    int y = YEAR.checkValidIntValue(fieldValues.remove(YEAR));
                    if (resolverStyle == ResolverStyle.LENIENT) {
                        long weeks = Jdk8Methods.safeSubtract(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
                        long days = Jdk8Methods.safeSubtract(fieldValues.remove(DAY_OF_WEEK), 1);
                        return date(y, 1, 1).plus(weeks, WEEKS).plus(days, DAYS);
                    }
                    int aw = ALIGNED_WEEK_OF_YEAR.checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR));
                    int dow = DAY_OF_WEEK.checkValidIntValue(fieldValues.remove(DAY_OF_WEEK));
                       HijrahDate date = date(y, 1, 1).plus(aw - 1, WEEKS).with(nextOrSame(DayOfWeek.of(dow)));
                    if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
                        throw new DateTimeException("Strict mode rejected date parsed to a different month");
                    }
                    return date;
                }
            }
        }
        return null;
    }

}
