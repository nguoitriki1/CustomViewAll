package com.example.progresscountdowntimer.pickerdate.datepicker

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import java.text.SimpleDateFormat
import java.util.*

class WheelMonthPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WheelPicker<String?>(
        context, attrs
    ) {
    private var lastScrollPosition = 0
    private var listener: MonthSelectedListener? = null
    private var displayMonthNumbers = false
    var monthFormat: String? = null
        get() = if (TextUtils.isEmpty(field)) {
            MONTH_FORMAT
        } else {
            field
        }

    override fun initFirst() {}
    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val monthList: MutableList<String> = ArrayList()
        val month_date = SimpleDateFormat(monthFormat, currentLocale)
        val cal = Calendar.getInstance(currentLocale)
        cal.timeZone = getDateHelper().getTimeZone()
        cal[Calendar.DAY_OF_MONTH] = 1
        for (i in 0..11) {
            cal[Calendar.MONTH] = i
            if (displayMonthNumbers) {
                monthList.add(String.format(Locale.getDefault(), "%02d", i + 1))
            } else {
                monthList.add(month_date.format(cal.time))
            }
        }
        return monthList
    }

    override fun initDefault(): String {
        return getDateHelper().getMonth(getDateHelper().today()).toString()
    }

    fun setOnMonthSelectedListener(listener: MonthSelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
        if (listener != null) {
            listener!!.onMonthSelected(this, position, item)
        }
    }

    override fun onItemCurrentScroll(position: Int, item: String?) {
        if (lastScrollPosition != position) {
            onItemSelected(position, item)
            lastScrollPosition = position
        }
    }

    fun displayMonthNumbers(): Boolean {
        return displayMonthNumbers
    }

    fun setDisplayMonthNumbers(displayMonthNumbers: Boolean) {
        this.displayMonthNumbers = displayMonthNumbers
    }

    val currentMonth: Int
        get() = getCurrentItemPosition()

    interface MonthSelectedListener {
        fun onMonthSelected(picker: WheelMonthPicker?, monthIndex: Int, monthName: String?)
    }

    companion object {
        const val MONTH_FORMAT = "MMMM"
    }
}