package com.example.progresscountdowntimer.pickerdate

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.pickerdate.datepicker.*
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelMonthPicker.Companion.MONTH_FORMAT
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelYearPicker.OnYearSelectedListener
import java.text.SimpleDateFormat
import java.util.*

class SingleDateAndTimePicker(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)
    private val BOLD = 0
    private val MEDIUM = 1
    private val REGULAR = 2
    private var dateHelper = DateHelper()
    private val yearsPicker: WheelYearPicker
    private val monthPicker: WheelMonthPicker
    private val daysOfMonthPicker: WheelDayOfMonthPicker
    private val daysPicker: WheelDayPicker
    private val pickers: MutableList<WheelPicker<*>> = ArrayList()
    private val listeners: MutableList<OnDateChangedListener> = ArrayList()
    private val dtSelector: View
    private var mustBeOnFuture = false
    private var minDate: Date? = null
    private var maxDate: Date? = null
    private var defaultDate: Date
    private var displayYears = false
    private var displayMonth = false
    private var displayDaysOfMonth = false
    private var displayDays = false
    fun setDateHelper(dateHelper: DateHelper) {
        this.dateHelper = dateHelper
    }

    fun setTimeZone(timeZone: TimeZone) {
        dateHelper.setTimeZone(timeZone)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        yearsPicker.setOnYearSelectedListener(object : OnYearSelectedListener {
            override fun onYearSelected(picker: WheelYearPicker?, position: Int, year: Int) {
                picker?.let {
                    updateListener()
                    checkMinMaxDate(picker)
                    if (displayDaysOfMonth) {
                        updateDaysOfMonth()
                    }
                }
            }

        })

        monthPicker.setOnMonthSelectedListener(object : WheelMonthPicker.MonthSelectedListener {
            override fun onMonthSelected(
                picker: WheelMonthPicker?,
                monthIndex: Int,
                monthName: String?
            ) {
                picker?.let {
                    updateListener()
                    checkMinMaxDate(picker)
                    if (displayDaysOfMonth) {
                        updateDaysOfMonth()
                    }
                }
            }

        })

        daysOfMonthPicker.setDayOfMonthSelectedListener(object :
            WheelDayOfMonthPicker.DayOfMonthSelectedListener {
            override fun onDayOfMonthSelected(picker: WheelDayOfMonthPicker?, dayIndex: Int) {
                picker?.let {
                    updateListener()
                    checkMinMaxDate(picker)
                }
            }

        })

        daysOfMonthPicker.setOnFinishedLoopListener(object :
            WheelDayOfMonthPicker.FinishedLoopListener {
            override fun onFinishedLoop(picker: WheelDayOfMonthPicker?) {
//                picker?.let {
//                    if (displayMonth) {
////                        monthPicker.scrollTo(monthPicker.getCurrentItemPosition() + 1)
////                        updateDaysOfMonth()
//                    }
//                }
            }
        })

        daysPicker.setOnDaySelectedListener(object : WheelDayPicker.OnDaySelectedListener {

            override fun onDaySelected(
                picker: WheelDayPicker?,
                position: Int,
                name: String?,
                date: Date?
            ) {
                picker?.let {
                    updateListener()
                    checkMinMaxDate(picker)
                }
            }
        })

        setDefaultDate(defaultDate)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        for (picker in pickers) {
            picker.isEnabled = enabled
        }
    }

    fun setDisplayYears(displayYears: Boolean) {
        this.displayYears = displayYears
        yearsPicker.visibility = if (displayYears) VISIBLE else GONE
    }

    fun setDisplayMonths(displayMonths: Boolean) {
        displayMonth = displayMonths
        monthPicker.visibility = if (displayMonths) VISIBLE else GONE
        checkSettings()
    }

    fun setDisplayDaysOfMonth(displayDaysOfMonth: Boolean) {
        this.displayDaysOfMonth = displayDaysOfMonth
        daysOfMonthPicker.visibility = if (displayDaysOfMonth) VISIBLE else GONE
        if (displayDaysOfMonth) {
            updateDaysOfMonth()
        }
        checkSettings()
    }

    fun setDisplayDays(displayDays: Boolean) {
        this.displayDays = displayDays
        daysPicker.visibility = if (displayDays) VISIBLE else GONE
        checkSettings()
    }

    fun setDisplayMonthNumbers(displayMonthNumbers: Boolean) {
        monthPicker.setDisplayMonthNumbers(displayMonthNumbers)
        monthPicker.updateAdapter()
    }

    fun setMonthFormat(monthFormat: String?) {
        monthPicker.monthFormat = monthFormat
        monthPicker.updateAdapter()
    }

    fun setTodayText(todayText: DateWithLabel?) {
        if (todayText != null && todayText.label.isNotEmpty()) {
            daysPicker.setTodayText(todayText)
        }
    }

    fun setItemSpacing(size: Int) {
        for (picker in pickers) {
            picker.itemSpace = size
        }
    }

    fun setCurvedMaxAngle(angle: Int) {
        for (picker in pickers) {
            picker.setCurvedMaxAngle(angle)
        }
    }

    fun setCurved(curved: Boolean) {
        for (picker in pickers) {
            picker.setCurved(curved)
        }
    }

    fun setCyclic(cyclic: Boolean) {
        for (picker in pickers) {
            picker.setCyclic(cyclic)
        }
    }

    fun setTextSize(textSize: Int) {
        for (picker in pickers) {
            picker.itemTextSize = textSize
        }
    }

    fun setSelectedTextColor(selectedTextColor: Int) {
        for (picker in pickers) {
            picker.selectedItemTextColor = selectedTextColor
        }
    }

    fun setTextColor(textColor: Int) {
        for (picker in pickers) {
            picker.itemTextColor = textColor
        }
    }

    fun setTextAlign(align: Int) {
        for (picker in pickers) {
            picker.itemAlign = align
        }
    }

    fun setTypefaceSelected(typeface: Typeface?) {
        if (typeface == null) return
        for (picker in pickers) {
            picker.setTypefaceSelected(typeface)
        }
    }

    fun setTypefaceNormal(typeface: Typeface?) {
        if (typeface == null) return
        for (picker in pickers) {
            picker.setTypefaceNormal(typeface)
        }
    }

    private fun setFontSelected(typeface: Typeface) {
        for (i in pickers.indices) {
            pickers[i].setTypefaceSelected(typeface)
        }
    }

    private fun setFontNormalToAllPickers(typeface: Typeface) {
        for (i in pickers.indices) {
            pickers[i].setTypefaceNormal(typeface)
        }
    }

    fun setSelectorColor(selectorColor: Int) {
        dtSelector.setBackgroundColor(selectorColor)
    }

    fun setVisibleItemCount(visibleItemCount: Int) {
        for (picker in pickers) {
            picker.visibleItemCount = visibleItemCount
        }
    }


    fun setDayFormatter(simpleDateFormat: SimpleDateFormat?) {
        if (simpleDateFormat != null) {
            daysPicker.setDayFormatter(simpleDateFormat)
        }
    }

    fun getMinDate(): Date? {
        return minDate
    }

    fun setMinDate(minDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.getTimeZone()
        calendar.time = minDate
        this.minDate = calendar.time
        setMinYear()
    }

    fun getMaxDate(): Date? {
        return maxDate
    }

    fun setMaxDate(maxDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.getTimeZone()
        calendar.time = maxDate
        this.maxDate = calendar.time
        setMinYear()
    }

    fun setCustomLocale(locale: Locale?) {
        for (p in pickers) {
            p.setCustomLocale(locale)
            p.updateAdapter()
        }
    }

    private fun checkMinMaxDate(picker: WheelPicker<*>) {
        checkBeforeMinDate(picker)
        checkAfterMaxDate(picker)
    }

    private fun checkBeforeMinDate(picker: WheelPicker<*>) {
        picker.postDelayed({
            if (minDate != null && isBeforeMinDate(date)) {
                for (p in pickers) {
                    p.scrollTo(p.findIndexOfDate(minDate!!))
                }
            }
        }, DELAY_BEFORE_CHECK_PAST.toLong())
    }

    private fun checkAfterMaxDate(picker: WheelPicker<*>) {
        picker.postDelayed({
            if (maxDate != null && isAfterMaxDate(date)) {
                for (p in pickers) {
                    p.scrollTo(p.findIndexOfDate(maxDate!!))
                }
            }
        }, DELAY_BEFORE_CHECK_PAST.toLong())
    }

    private fun isBeforeMinDate(date: Date): Boolean {
        return dateHelper.getCalendarOfDate(date).before(dateHelper.getCalendarOfDate(minDate))
    }

    private fun isAfterMaxDate(date: Date): Boolean {
        return dateHelper.getCalendarOfDate(date).after(dateHelper.getCalendarOfDate(maxDate))
    }

    fun addOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.add(listener)
    }

    fun removeOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.remove(listener)
    }

    fun checkPickersMinMax() {
        for (picker in pickers) {
            checkMinMaxDate(picker)
        }
    }

    private val date: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            if (displayDays) {
                daysPicker.currentDate?.let {
                    calendar.time = it
                }
            } else {
                if (displayMonth) {
                    calendar[Calendar.MONTH] = monthPicker.currentMonth
                }
                if (displayYears) {
                    calendar[Calendar.YEAR] = yearsPicker.currentYear
                }
                if (displayDaysOfMonth) {
                    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (daysOfMonthPicker.currentDay >= daysInMonth) {
                        calendar[Calendar.DAY_OF_MONTH] = daysInMonth
                    } else {
                        calendar[Calendar.DAY_OF_MONTH] = daysOfMonthPicker.currentDay + 1
                    }
                }
            }
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.time
        }

    fun getSelectedDate(): Date {
        return date
    }

    fun setDefaultDate(date: Date?) {
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            calendar.time = date
            defaultDate = calendar.time
            updateDaysOfMonth(calendar)
            for (picker in pickers) {
                picker.setDefaultDate(defaultDate)
            }
        }
    }

    fun selectDate(calendar: Calendar?) {
        if (calendar == null) {
            return
        }
        val date = calendar.time
        for (picker in pickers) {
            picker.selectDate(date)
        }
        if (displayDaysOfMonth) {
            updateDaysOfMonth()
        }
    }

    private fun updateListener() {
        val date = date
        val format = FORMAT_24_HOUR
        val displayed = DateFormat.format(format, date).toString()
        for (listener in listeners) {
            listener.onDateChanged(displayed, date)
        }
    }

    private fun updateDaysOfMonth() {
        val date = date
        val calendar = Calendar.getInstance()
        calendar.timeZone = dateHelper.getTimeZone()
        calendar.time = date
        updateDaysOfMonth(calendar)
    }

    private fun updateDaysOfMonth(calendar: Calendar) {
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        daysOfMonthPicker.daysInMonth = daysInMonth
        daysOfMonthPicker.updateAdapter()
    }

    private fun setMustBeOnFuture(mustBeOnFuture: Boolean) {
        this.mustBeOnFuture = mustBeOnFuture
        daysPicker.setShowOnlyFutureDate(mustBeOnFuture)
        if (mustBeOnFuture) {
            val now = Calendar.getInstance()
            now.timeZone = dateHelper.getTimeZone()
            minDate = now.time //minDate is Today
        }
    }

    private fun setMinYear() {
        if (displayYears && minDate != null && maxDate != null) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            calendar.time = minDate!!
            yearsPicker.setMinYear(calendar[Calendar.YEAR])
            calendar.time = maxDate!!
            yearsPicker.setMaxYear(calendar[Calendar.YEAR])
        }
    }

    private fun checkSettings() {
        require(!(displayDays && (displayDaysOfMonth || displayMonth))) { "You can either display days with months or days and months separately" }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SingleDateAndTimePicker)
        val resources = resources
        setTextColor(
            a.getColor(
                R.styleable.SingleDateAndTimePicker_picker_textColor,
                ContextCompat.getColor(context, R.color.picker_default_text_color)
            )
        )
        setSelectedTextColor(
            a.getColor(
                R.styleable.SingleDateAndTimePicker_picker_selectedTextColor,
                ContextCompat.getColor(context, R.color.picker_default_selected_text_color)
            )
        )
        setSelectorColor(
            a.getColor(
                R.styleable.SingleDateAndTimePicker_picker_selectorColor,
                ContextCompat.getColor(context, R.color.picker_default_selector_color)
            )
        )
        setItemSpacing(
            a.getDimensionPixelSize(
                R.styleable.SingleDateAndTimePicker_picker_itemSpacing,
                resources.getDimensionPixelSize(R.dimen.wheelSelectorHeight)
            )
        )
        setCurvedMaxAngle(
            a.getInteger(
                R.styleable.SingleDateAndTimePicker_picker_curvedMaxAngle,
                WheelPicker.Companion.MAX_ANGLE
            )
        )
        setTextSize(
            a.getDimensionPixelSize(
                R.styleable.SingleDateAndTimePicker_picker_textSize,
                resources.getDimensionPixelSize(R.dimen.WheelItemTextSize)
            )
        )
        setCurved(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_curved,
                IS_CURVED_DEFAULT
            )
        )
        setCyclic(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_cyclic,
                IS_CYCLIC_DEFAULT
            )
        )
        setMustBeOnFuture(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_mustBeOnFuture,
                MUST_BE_ON_FUTURE_DEFAULT
            )
        )
        setVisibleItemCount(
            a.getInt(
                R.styleable.SingleDateAndTimePicker_picker_visibleItemCount,
                VISIBLE_ITEM_COUNT_DEFAULT
            )
        )
        daysPicker.setDayCount(
            a.getInt(
                R.styleable.SingleDateAndTimePicker_picker_dayCount,
                SingleDateAndTimeConstants.DAYS_PADDING
            )
        )
        setDisplayDays(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_displayDays,
                displayDays
            )
        )
        setDisplayMonths(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_displayMonth,
                displayMonth
            )
        )
        setDisplayYears(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_displayYears,
                displayYears
            )
        )
        setDisplayDaysOfMonth(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_displayDaysOfMonth,
                displayDaysOfMonth
            )
        )
        setDisplayMonthNumbers(
            a.getBoolean(
                R.styleable.SingleDateAndTimePicker_picker_displayMonthNumbers,
                monthPicker.displayMonthNumbers()
            )
        )
        val fontSelected = a.getInt(R.styleable.SingleDateAndTimePicker_fontFamilyTextSelect, BOLD)
        val fontNormal = a.getInt(R.styleable.SingleDateAndTimePicker_fontFamilyTextSelect, MEDIUM)
        var fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
        var fontNormalTypeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        when (fontSelected) {
            MEDIUM -> {
                fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_medium)
            }
            REGULAR -> {
                fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_regular)
            }
        }

        when (fontNormal) {
            MEDIUM -> {
                fontNormalTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_medium)
            }
            REGULAR -> {
                fontNormalTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_regular)
            }
        }

        fontSelectedTypeface?.let {
            setFontSelected(fontSelectedTypeface)
        }

        fontNormalTypeface?.let {
            setFontNormalToAllPickers(fontNormalTypeface)
        }

        val monthFormat = a.getString(R.styleable.SingleDateAndTimePicker_picker_monthFormat)
        setMonthFormat(if (TextUtils.isEmpty(monthFormat)) MONTH_FORMAT else monthFormat)
        setTextAlign(ALIGN_CENTER)
        checkSettings()
        setMinYear()
        a.recycle()
        if (displayDaysOfMonth) {
            val now = Calendar.getInstance()
            now.timeZone = dateHelper.getTimeZone()
            updateDaysOfMonth(now)
        }
        daysPicker.updateAdapter() // For MustBeFuture and dayCount
    }

    interface OnDateChangedListener {
        fun onDateChanged(displayed: String?, date: Date?)
    }

    companion object {
        const val IS_CYCLIC_DEFAULT = true
        const val IS_CURVED_DEFAULT = false
        const val MUST_BE_ON_FUTURE_DEFAULT = false
        const val DELAY_BEFORE_CHECK_PAST = 200
        const val VISIBLE_ITEM_COUNT_DEFAULT = 5
        const val ALIGN_CENTER = 0
        private val FORMAT_24_HOUR: CharSequence = "EEE d MMM H:mm"
    }

    init {
        defaultDate = Date()
        inflate(context, R.layout.single_day_and_time_picker, this)
        yearsPicker = findViewById(R.id.yearPicker)
        monthPicker = findViewById(R.id.monthPicker)
        daysOfMonthPicker = findViewById(R.id.daysOfMonthPicker)
        daysPicker = findViewById(R.id.daysPicker)
        dtSelector = findViewById(R.id.dtSelector)
        pickers.addAll(
            listOf(
                daysPicker,
                daysOfMonthPicker,
                monthPicker,
                yearsPicker
            )
        )
        for (wheelPicker in pickers) {
            wheelPicker.setDateHelper(dateHelper)
        }
        init(context, attrs)
    }
}