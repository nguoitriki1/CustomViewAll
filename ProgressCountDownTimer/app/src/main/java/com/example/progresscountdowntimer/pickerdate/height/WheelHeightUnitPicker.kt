package com.example.progresscountdowntimer.pickerdate.height

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelHeightUnitPicker : WheelPicker<String?> {
    private var listener: WheelHeightUnitListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    override fun initFirst() {}
    override fun initDefault(): String {
        return getLocalizedString(R.string.picker_height_cm)
    }

    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String>? {
        return listOf(
            getLocalizedString(R.string.picker_height_cm),
            getLocalizedString(R.string.picker_height_ft)
        )
    }

    fun setHeightUnitListener(wheelHeightUnitListener: WheelHeightUnitListener?) {
        listener = wheelHeightUnitListener
    }

    override fun onItemSelected(position: Int, item: String?) {
        super.onItemSelected(position, item)
        if (listener != null) {
            listener!!.onHeightUnitChange(this, isCm)
        }
    }

    override fun setCyclic(isCyclic: Boolean) {
        super.setCyclic(false)
    }

    fun isCmPosition(position: Int): Boolean {
        return position == INDEX_CM
    }

    override fun getFormattedValue(value: Any): String {
        if (value is Date) {
//            return getLocalizedString(instance.get() == Calendar.PM ? R.string.picker_height_ft : R.string.picker_height_cm);
        }
        return value.toString()
    }

    private val isCm: Boolean
        get() = getCurrentItemPosition() == INDEX_CM
    private val isFt: Boolean
        get() = getCurrentItemPosition() == INDEX_FT

    interface WheelHeightUnitListener {
        fun onHeightUnitChange(heightUnitPicker: WheelHeightUnitPicker?, isCm: Boolean)
    }

    companion object {
        const val INDEX_CM = 0
        const val INDEX_FT = 1
    }
}