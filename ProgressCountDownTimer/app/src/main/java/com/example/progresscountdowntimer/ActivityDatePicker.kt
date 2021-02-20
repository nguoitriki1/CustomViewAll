package com.example.progresscountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.progresscountdowntimer.pickerdate.SingleDateAndTimePicker
import com.example.progresscountdowntimer.pickerdate.height.HeightPickerView
import com.example.progresscountdowntimer.pickerdate.weight.WeightPickerView
import java.util.*

class ActivityDatePicker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_picker)
        val heightPicker = findViewById<HeightPickerView>(R.id.picker_height)
        val weightPicker = findViewById<WeightPickerView>(R.id.picker_weight)
        val datePicker = findViewById<SingleDateAndTimePicker>(R.id.picker_date)
        heightPicker.addOnHeightChangedListener(object : HeightPickerView.OnHeightChangedListener{
            override fun onHeightChanged(displayed: String?, value: String?, isCm: Boolean) {
                
            }

        })
        heightPicker.setOnClickListener {
            val fitValue = heightPicker.fitValue
            val fitValue2 = heightPicker.fitValue2
            val unitHeight = heightPicker.unitHeight
            Log.d("abc","fitValue : $fitValue,fitValue2 : $fitValue2,unitHeight : $unitHeight")
        }

        weightPicker.addOnWeightChangedListener(object : WeightPickerView.OnWeightChangedListener{
            override fun onWeightChanged(displayed: String?, value: String?, isCm: Boolean) {
            }

        })
        weightPicker.setOnClickListener {
            val fitValue = weightPicker.fitValue
            val fitValue2 = weightPicker.fitValue2
            val unitHeight = weightPicker.unitHeight
            Log.d("abc","fitValue : $fitValue,fitValue2 : $fitValue2,unitHeight : $unitHeight")
        }


        datePicker.addOnDateChangedListener(object : SingleDateAndTimePicker.OnDateChangedListener{
            override fun onDateChanged(displayed: String?, date: Date?) {
                Log.d("abc","displayed : $displayed,date : $date")
            }
        })

        val selectedDate = datePicker.getSelectedDate()
    }
}