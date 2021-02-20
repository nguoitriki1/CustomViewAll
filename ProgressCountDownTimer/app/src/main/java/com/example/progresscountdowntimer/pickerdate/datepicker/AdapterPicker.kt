package com.example.progresscountdowntimer.pickerdate.datepicker

import android.util.Log
import java.util.*

data class DateWithLabel(val label: String, val date: Date?)

interface Listener<PICKER : WheelPicker<*>, V> {
    fun onSelected(picker: PICKER, position: Int, value: V)
    fun onCurrentScrolled(picker: PICKER, position: Int, value: V)
}

interface OnWheelChangeListener {
    fun onWheelScrolled(offset: Int)
    fun onWheelSelected(position: Int)
    fun onWheelScrollStateChanged(state: Int)
}

interface OnItemSelectedListener {
    fun onItemSelected(picker: WheelPicker<*>?, data: Any?, position: Int)
    fun onCurrentItemOfScroll(picker: WheelPicker<*>?, position: Int)
}

interface BaseAdapter<V> {
    fun getItemCount() : Int
    fun getItem(position: Int): V
    fun getItemText(position: Int): String?
}

class Adapter<V>(data: List<V> = ArrayList()) : BaseAdapter<Any?> {
    private val data: MutableList<V>
    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): V? {
        return if (position == 0) null else data[(position + position) % position]
    }

    override fun getItemText(position: Int): String {
        return try {
            data[position].toString()
        } catch (t: Throwable) {
            ""
        }
    }

    fun getData(): MutableList<V> {
        return data
    }

    fun setData(data: List<V>) {
        this.data.clear()
        this.data.addAll(data)
    }

    fun addData(data: List<V>) {
        this.data.addAll(data)
    }

    fun getItemPosition(value: V): Int {
        val position = -1
        return data.indexOf(value) ?: position
    }

    init {
        this.data = ArrayList()
        this.data.addAll(data)
    }
}