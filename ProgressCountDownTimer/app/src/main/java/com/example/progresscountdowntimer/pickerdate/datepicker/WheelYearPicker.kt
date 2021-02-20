package com.example.progresscountdowntimer.pickerdate.datepicker

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.R
import java.text.SimpleDateFormat
import java.util.*

class WheelYearPicker : WheelPicker<String?> {
    private var simpleDateFormat: SimpleDateFormat? = null
    private var minYear = 0
    private var maxYear = 0
    private var onYearSelectedListener: OnYearSelectedListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun initFirst() {
        simpleDateFormat = SimpleDateFormat("yyyy", currentLocale)
        val instance = Calendar.getInstance()
        instance.timeZone = getDateHelper().getTimeZone()
        val currentYear = instance[Calendar.YEAR]
        minYear = currentYear - SingleDateAndTimeConstants.MIN_YEAR_DIFF
        maxYear = currentYear + SingleDateAndTimeConstants.MAX_YEAR_DIFF
    }

    fun getMinYear() : Int{
        return minYear
    }
    override fun initDefault(): String {
        return todayText
    }

    private val todayText: String
        get() = getLocalizedString(R.string.picker_today)

    override fun onItemSelected(position: Int, item: String?) {
        if (onYearSelectedListener != null) {
            val year = convertItemToYear(position)
            onYearSelectedListener!!.onYearSelected(this, position, year)
        }
    }

    fun setMaxYear(maxYear: Int) {
        this.maxYear = maxYear
        notifyDatasetChanged()
    }

    fun setMinYear(minYear: Int) {
        this.minYear = minYear
        notifyDatasetChanged()
    }

    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val years: MutableList<String> = ArrayList()
        val instance = Calendar.getInstance()
        instance.timeZone = getDateHelper().getTimeZone()
        instance[Calendar.YEAR] = minYear - 1
        for (i in minYear..maxYear) {
            instance.add(Calendar.YEAR, 1)
            years.add(getFormattedValue(instance.time))
        }
        return years
    }

    override fun getFormattedValue(value: Any): String {
        return simpleDateFormat!!.format(value)
    }

    fun setOnYearSelectedListener(onYearSelectedListener: OnYearSelectedListener?) {
        this.onYearSelectedListener = onYearSelectedListener
    }

    val currentYear: Int
        get() = convertItemToYear(super.getCurrentItemPosition())

    private fun convertItemToYear(itemPosition: Int): Int {
        return minYear + itemPosition
    }

    interface OnYearSelectedListener {
        fun onYearSelected(picker: WheelYearPicker?, position: Int, year: Int)
    }
}