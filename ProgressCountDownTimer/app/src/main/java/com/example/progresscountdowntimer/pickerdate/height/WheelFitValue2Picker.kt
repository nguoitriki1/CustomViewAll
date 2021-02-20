package com.example.progresscountdowntimer.pickerdate.height

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelFitValue2Picker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    WheelPicker<String?>(context, attrs) {
    private var isCm: Boolean = true
    private var lastScrollPosition = 0
    private var listener: HeightValue2SelectedListener? = null
    private val DEFAULT_FIT = "1"

    override fun initFirst() {}
    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        val fList: MutableList<String> = ArrayList()
        if (isCm){
            for (i in 1..250) {
                fList.add(String.format(Locale.getDefault(), "%02d", i))

            }
        }else{
            for (i in 1..11) {
                fList.add(String.format(Locale.getDefault(), "%02d", i))

            }
        }
        return fList
    }

    override fun initDefault(): String {
        return DEFAULT_FIT
    }

    fun setOnHeightValueSelectedListener(listener: HeightValue2SelectedListener?) {
        this.listener = listener
    }

    override fun onItemSelected(position: Int, item: String?) {
            listener?.onFitHeight2Selected(this, position, item)
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

    interface HeightValue2SelectedListener {
        fun onFitHeight2Selected(picker: WheelFitValue2Picker?, index: Int, name: String?)
    }
}