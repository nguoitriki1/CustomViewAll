package com.example.progresscountdowntimer.pickerdate.weight

import android.content.Context
import android.util.AttributeSet
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.pickerdate.datepicker.WheelPicker
import java.util.*

class WheelWeightUnitPicker : WheelPicker<String?> {
    private var listener: WheelWeightUnitListener? = null
    private val isKg: Boolean
        get() = getCurrentItemPosition() == INDEX_KG
    private val isLBS: Boolean
        get() = getCurrentItemPosition() == INDEX_LBS


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    override fun initFirst() {}
    override fun initDefault(): String {
        return getLocalizedString(R.string.picker_weight_kg)
    }

    override fun generateAdapterValues(showOnlyFutureDates: Boolean): List<String> {
        return listOf(
            getLocalizedString(R.string.picker_weight_kg),
            getLocalizedString(R.string.picker_weight_lbs)
        )
    }

    fun setWeightUnitListener(wheelWeightUnitListener: WheelWeightUnitListener) {
        listener = wheelWeightUnitListener
    }

    override fun onItemSelected(position: Int, item: String?) {
        super.onItemSelected(position, item)
        listener?.onWeightUnitChange(this, isKg)
    }

    override fun setCyclic(isCyclic: Boolean) {
        super.setCyclic(false)
    }

    fun isKgPosition(position: Int): Boolean {
        return position == INDEX_KG
    }

    override fun getFormattedValue(value: Any): String {
        return value.toString()
    }

    interface WheelWeightUnitListener {
        fun onWeightUnitChange(weightPicker: WheelWeightUnitPicker?, isKg: Boolean)
    }

    companion object {
        const val INDEX_KG = 0
        const val INDEX_LBS = 1
    }
}