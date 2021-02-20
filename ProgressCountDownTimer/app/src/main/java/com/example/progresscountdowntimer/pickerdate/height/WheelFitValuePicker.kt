package com.example.progresscountdowntimer.pickerdate.height

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelFitValuePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : WheelPicker<String?>(context, attrs) {
    private var lastScrollPosition = 0
    private var listener: HeightValueSelectedListener? = null
    private val DEFAULT_FIT = "1"

    override fun initFirst() {}
    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val fList: MutableList<String> = ArrayList()
        for (i in 1..7) {
            fList.add(String.format(Locale.getDefault(), "%02d", i))

        }
        return fList
    }

    override fun initDefault(): String {
        return DEFAULT_FIT
    }

    fun setOnHeightValueSelectedListener(listener: HeightValueSelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
            listener?.onFitHeightSelected(this, position, item)
    }

    override fun onItemCurrentScroll(position: Int, item: String?) {
        if (lastScrollPosition != position) {
            onItemSelected(position, item)
            lastScrollPosition = position
        }
    }

    val currentValuePicker: Int
        get() = getCurrentItemPosition()

    interface HeightValueSelectedListener {
        fun onFitHeightSelected(picker: WheelFitValuePicker?, index: Int, name: String?)
    }
}