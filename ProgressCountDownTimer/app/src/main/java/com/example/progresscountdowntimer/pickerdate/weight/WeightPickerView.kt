package com.example.progresscountdowntimer.pickerdate.weight

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.pickerdate.SingleDateAndTimePicker
import com.example.progresscountdowntimer.pickerdate.datepicker.*
import java.util.*

class WeightPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val BOLD = 0
    private val MEDIUM = 1
    private val REGULAR = 2

    private val UNIT = 3
    private val VALUE1 = 4
    private val VALUE2 = 5
    private val unitWeightPicker: WheelWeightUnitPicker
    private val weightValuePicker: WheelWeightValuePicker
    private val weightValue2Picker: WheelWeightValue2Picker
    private val pickers: MutableList<WheelPicker<*>> = ArrayList()
    private val listeners: MutableList<OnWeightChangedListener> = ArrayList()
    private var isKgValue: Boolean = true

    init {
        inflate(context, R.layout.single_weight_picker_layout, this)
        unitWeightPicker = findViewById(R.id.weightUnitPicker)
        weightValuePicker = findViewById(R.id.weightValuePicker)
        weightValue2Picker = findViewById(R.id.weightValue2Picker)
        pickers.addAll(
            listOf(
                weightValuePicker,
                weightValue2Picker,
                unitWeightPicker
            )
        )

        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SingleHeightPicker)
        val resources = resources
        setTextColor(
            a.getColor(
                R.styleable.SingleHeightPicker_picker_height_textColor,
                ContextCompat.getColor(context, R.color.picker_default_text_color)
            )
        )
        setSelectedTextColor(
            a.getColor(
                R.styleable.SingleHeightPicker_picker_height_selectedTextColor,
                ContextCompat.getColor(context, R.color.picker_default_selected_text_color)
            )
        )
        setItemSpacing(
            a.getDimensionPixelSize(
                R.styleable.SingleHeightPicker_picker_height_itemSpacing,
                resources.getDimensionPixelSize(R.dimen.wheelSelectorHeight)
            )
        )
        setCurvedMaxAngle(
            a.getInteger(
                R.styleable.SingleHeightPicker_picker_height_curvedMaxAngle,
                WheelPicker.Companion.MAX_ANGLE
            )
        )
        setTextSize(
            a.getDimensionPixelSize(
                R.styleable.SingleHeightPicker_picker_height_textSize,
                resources.getDimensionPixelSize(R.dimen.WheelItemTextSize)
            )
        )
        setCurved(
            a.getBoolean(
                R.styleable.SingleHeightPicker_picker_height_curved,
                SingleDateAndTimePicker.IS_CURVED_DEFAULT
            )
        )
        setCyclic(
            a.getBoolean(
                R.styleable.SingleHeightPicker_picker_height_cyclic,
                SingleDateAndTimePicker.IS_CYCLIC_DEFAULT
            )
        )
        setVisibleItemCount(
            a.getInt(
                R.styleable.SingleHeightPicker_picker_height_visibleItemCount,
                SingleDateAndTimePicker.VISIBLE_ITEM_COUNT_DEFAULT
            )
        )
        val fontSelected =
            a.getInt(R.styleable.SingleHeightPicker_picker_height_fontFamilyTextSelect, BOLD)
        val fontNormal =
            a.getInt(R.styleable.SingleHeightPicker_picker_height_fontFamilyTextSelect, MEDIUM)
        var fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
        var fontNormalTypeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        when (fontSelected) {
            MEDIUM -> {
                fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_medium)
            }
            REGULAR -> {
                fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_regular)
            }
        }

        when (fontNormal) {
            MEDIUM -> {
                fontNormalTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_medium)
            }
            REGULAR -> {
                fontNormalTypeface = ResourcesCompat.getFont(context, R.font.sanfrancisco_regular)
            }
        }

        fontSelectedTypeface?.let {
            setFontSelected(fontSelectedTypeface)
        }

        fontNormalTypeface?.let {
            setFontNormalToAllPickers(fontNormalTypeface)
        }

        setTextAlign(SingleDateAndTimePicker.ALIGN_CENTER)
        a.recycle()
        unitChanged(isKgValue)
    }

    fun setTextColor(textColor: Int) {
        for (picker in pickers) {
            picker.itemTextColor = textColor
        }
    }

    private fun setSelectedTextColor(color: Int) {
        for (picker in pickers) {
            picker.selectedItemTextColor = color
        }
    }

    fun setItemSpacing(size: Int) {
        for (picker in pickers) {
            picker.itemSpace = size
        }
    }

    private fun setFontSelected(typeface: Typeface) {
        for (i in pickers.indices) {
            pickers[i].setTypefaceSelected(typeface)
        }
    }

    private fun setFontNormalToAllPickers(typeface: Typeface) {
        for (i in pickers.indices) {
            pickers[i].setTypefaceNormal(typeface)
        }
    }

    fun setCurvedMaxAngle(angle: Int) {
        for (picker in pickers) {
            picker.setCurvedMaxAngle(angle)
        }
    }

    fun setCurved(curved: Boolean) {
        for (picker in pickers) {
            picker.setCurved(curved)
        }
    }

    fun setCyclic(cyclic: Boolean) {
        for (picker in pickers) {
            picker.setCyclic(cyclic)
        }
    }

    fun setTextSize(textSize: Int) {
        for (picker in pickers) {
            picker.itemTextSize = textSize
        }
    }

    fun setTextAlign(align: Int) {
        for (picker in pickers) {
            picker.itemAlign = align
        }
    }

    fun setVisibleItemCount(visibleItemCount: Int) {
        for (picker in pickers) {
            picker.visibleItemCount = visibleItemCount
        }
    }

    interface OnWeightChangedListener {
        fun onWeightChanged(displayed: String?, value: String?, isCm: Boolean)
    }

    fun addOnWeightChangedListener(listener: OnWeightChangedListener) {
        listeners.add(listener)
    }

    fun removeOnDateChangedListener(listener: OnWeightChangedListener) {
        listeners.remove(listener)
    }

    private fun updateListener(type: Int) {
        when (type) {
            UNIT -> {
                for (listener in listeners) {
                    listener.onWeightChanged("UNIT", unitHeight,isKgValue)
                }
            }
            VALUE1 -> {
                for (listener in listeners) {
                    listener.onWeightChanged("FIT1", fitValue,isKgValue)
                }
            }

            VALUE2 -> {
                for (listener in listeners) {
                    listener.onWeightChanged("FIT2", fitValue2,isKgValue)
                }
            }
        }
    }

    val fitValue2: String
        get() {
            return (weightValue2Picker.currentFitValue2 + 1).toString()
        }

    val fitValue: String
        get() {
            return (weightValuePicker.currentValuePicker + 1).toString()
        }

    val unitHeight: String
        get() {
            return unitWeightPicker.getCurrentItemPosition().toString()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        unitWeightPicker.setWeightUnitListener(object : WheelWeightUnitPicker.WheelWeightUnitListener {

            override fun onWeightUnitChange(
                weightPicker: WheelWeightUnitPicker?,
                isKg: Boolean
            ) {
                weightPicker?.let {
                    isKgValue = isKg
                    updateListener(UNIT)
                    unitChanged(isKg)
                }
            }
        })

        weightValuePicker.setOnWeightValueSelectedListener(object :
            WheelWeightValuePicker.WeightValueSelectedListener {
            override fun onValueWeightSelected(
                picker: WheelWeightValuePicker?,
                index: Int,
                name: String?
            ) {
                picker?.let {
                    updateListener(VALUE1)
                }
            }

        })

        weightValue2Picker.setOnWeightValueSelectedListener(object :
            WheelWeightValue2Picker.WeightValue2SelectedListener {
            override fun onValue2Weight2Selected(
                picker: WheelWeightValue2Picker?,
                index: Int,
                name: String?
            ) {
                updateListener(VALUE2)
            }

        })
    }

    private fun unitChanged(isKg: Boolean) {
        weightValuePicker.changeData(isKg)
        postInvalidate()
    }

}