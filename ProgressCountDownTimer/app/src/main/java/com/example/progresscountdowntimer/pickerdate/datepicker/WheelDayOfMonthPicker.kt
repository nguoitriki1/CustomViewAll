package com.example.progresscountdowntimer.pickerdate.datepicker

import android.content.Context
import android.util.AttributeSet
import java.util.*

class WheelDayOfMonthPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WheelPicker<String?>(
    context, attrs
) {
    var daysInMonth = 0
    private var listener: DayOfMonthSelectedListener? = null
    private var finishedLoopListener: FinishedLoopListener? = null
    override fun initFirst() {
        // no-op here
    }

    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val dayList: MutableList<String> = ArrayList()
        for (i in 1..daysInMonth) {
            dayList.add(String.format(Locale.getDefault(), "%02d", i))
        }
        return dayList
    }

    override fun initDefault(): String {
        return getDateHelper().getDay(getDateHelper().today()).toString()
    }

    fun setOnFinishedLoopListener(finishedLoopListener: FinishedLoopListener?) {
        this.finishedLoopListener = finishedLoopListener
    }

    override fun onFinishedLoop() {
        super.onFinishedLoop()
        if (finishedLoopListener != null) {
            finishedLoopListener!!.onFinishedLoop(this)
        }
    }

    fun setDayOfMonthSelectedListener(listener: DayOfMonthSelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
            listener?.onDayOfMonthSelected(this, position)
    }

    val currentDay: Int
        get() = getCurrentItemPosition()

    interface FinishedLoopListener {
        fun onFinishedLoop(picker: WheelDayOfMonthPicker?)
    }

    interface DayOfMonthSelectedListener {
        fun onDayOfMonthSelected(picker: WheelDayOfMonthPicker?, dayIndex: Int)
    }
}