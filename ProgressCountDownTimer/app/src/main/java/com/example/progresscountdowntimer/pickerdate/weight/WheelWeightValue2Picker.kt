package com.example.progresscountdowntimer.pickerdate.weight

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelWeightValue2Picker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WheelPicker<String?>(context, attrs) {
    private var isCm: Boolean = true
    private var lastScrollPosition = 0
    private var listener: WeightValue2SelectedListener? = null
    private val DEFAULT_FIT = "1"

    override fun initFirst() {}
    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val fList: MutableList<String> = ArrayList()
        for (i in 1..9) {
            fList.add(String.format(Locale.getDefault(), "%02d", i))

        }
        return fList
    }

    override fun initDefault(): String {
        return DEFAULT_FIT
    }

    fun setOnWeightValueSelectedListener(listener: WeightValue2SelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
            listener?.onValue2Weight2Selected(this, position, item)
    }

    override fun onItemCurrentScroll(position: Int, item: String?) {
        if (lastScrollPosition != position) {
            onItemSelected(position, item)
            lastScrollPosition = position
        }
    }

    fun changeData(cm: Boolean) {
        this.isCm = cm
        generateAdapterValues(false)
        updateAdapter()
    }

    val currentFitValue2: Int
        get() = getCurrentItemPosition()

    interface WeightValue2SelectedListener {
        fun onValue2Weight2Selected(picker: WheelWeightValue2Picker?, index: Int, name: String?)
    }
}