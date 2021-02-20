package com.example.progresscountdowntimer.pickerdate.weight

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelWeightValuePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WheelPicker<String?>(context, attrs) {
    private var lastScrollPosition = 0
    private var listener: WeightValueSelectedListener? = null
    private val DEFAULT_FIT = "15"
    private var isKg: Boolean = true

    override fun initFirst() {}
    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val fList: MutableList<String> = ArrayList()
        if (isKg) {
            for (i in 15..350) {
                fList.add(String.format(Locale.getDefault(), "%02d", i))

            }
        } else {
            for (i in 33..765) {
                fList.add(String.format(Locale.getDefault(), "%02d", i))

            }
        }
        return fList
    }

    override fun initDefault(): String {
        return DEFAULT_FIT
    }

    fun setOnWeightValueSelectedListener(listener: WeightValueSelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
        listener?.onValueWeightSelected(this, position, item)
    }

    override fun onItemCurrentScroll(position: Int, item: String?) {
        if (lastScrollPosition != position) {
            onItemSelected(position, item)
            lastScrollPosition = position
        }
    }

    fun changeData(isKg: Boolean) {
        this.isKg = isKg
        generateAdapterValues(false)
        updateAdapter()
    }

    val currentValuePicker: Int
        get() = getCurrentItemPosition()

    interface WeightValueSelectedListener {
        fun onValueWeightSelected(picker: WheelWeightValuePicker?, index: Int, name: String?)
    }
}