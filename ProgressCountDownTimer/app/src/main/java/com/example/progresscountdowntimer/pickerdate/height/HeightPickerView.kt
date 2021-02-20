package com.example.progresscountdowntimer.pickerdate.height

import android.content.Context
import android.graphics.Typeface
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.pickerdate.SingleDateAndTimePicker
import com.example.progresscountdowntimer.pickerdate.datepicker.*
import java.util.*

class HeightPickerView @JvmOverloads constructor(
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
    private val unitPicker: WheelHeightUnitPicker
    private val heightValuePicker: WheelFitValuePicker
    private val viewSpace1: View
    private val viewSpace2: View
    private val heightValue2Picker: WheelFitValue2Picker
    private val pickers: MutableList<WheelPicker<*>> = ArrayList()
    private val listeners: MutableList<OnHeightChangedListener> = ArrayList()
    private var isCmValue: Boolean = true

    init {
        inflate(context, R.layout.single_height_picker_layout, this)
        unitPicker = findViewById(R.id.heightUnitPicker)
        viewSpace1 = findViewById(R.id.view_space1)
        viewSpace2 = findViewById(R.id.view_space2)
        heightValuePicker = findViewById(R.id.heightValuePicker)
        heightValue2Picker = findViewById(R.id.heightValue2Picker)
        pickers.addAll(
            listOf(
                heightValuePicker,
                heightValue2Picker,
                unitPicker
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
        unitChanged(isCmValue)
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

    interface OnHeightChangedListener {
        fun onHeightChanged(displayed: String?, value: String?, isCm: Boolean)
    }

    fun addOnHeightChangedListener(listener: OnHeightChangedListener) {
        listeners.add(listener)
    }

    fun removeOnDateChangedListener(listener: OnHeightChangedListener) {
        listeners.remove(listener)
    }

    private fun updateListener(type: Int) {
        when (type) {
            UNIT -> {
                for (listener in listeners) {
                    listener.onHeightChanged("UNIT", unitHeight,isCmValue)
                }
            }
            VALUE1 -> {
                for (listener in listeners) {
                    listener.onHeightChanged("FIT1", fitValue,isCmValue)
                }
            }

            VALUE2 -> {
                for (listener in listeners) {
                    listener.onHeightChanged("FIT2", fitValue2,isCmValue)
                }
            }
        }
    }

    val fitValue2: String
        get() {
            return (heightValue2Picker.currentFitValue2 + 1).toString()
        }

    val fitValue: String
        get() {
            if (isCmValue){
                return ""
            }
            return (heightValuePicker.currentValuePicker + 1).toString()
        }

    val unitHeight: String
        get() {
            return unitPicker.getCurrentItemPosition().toString()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        unitPicker.setHeightUnitListener(object : WheelHeightUnitPicker.WheelHeightUnitListener {
            override fun onHeightUnitChange(
                heightUnitPicker: WheelHeightUnitPicker?,
                isCm: Boolean
            ) {
                heightUnitPicker?.let {
                    isCmValue = isCm
                    updateListener(UNIT)
                    unitChanged(isCm)
                }
            }
        })

        heightValuePicker.setOnHeightValueSelectedListener(object :
            WheelFitValuePicker.HeightValueSelectedListener {
            override fun onFitHeightSelected(
                picker: WheelFitValuePicker?,
                index: Int,
                name: String?
            ) {
                picker?.let {
                    updateListener(VALUE1)
                }
            }
        })

        heightValue2Picker.setOnHeightValueSelectedListener(object :
            WheelFitValue2Picker.HeightValue2SelectedListener {
            override fun onFitHeight2Selected(
                picker: WheelFitValue2Picker?,
                index: Int,
                name: String?
            ) {
                picker?.let {
                    updateListener(VALUE2)
                }
            }
        })
    }

    private fun unitChanged(isCm: Boolean) {
        heightValue2Picker.changeData(isCm)
        if (isCm) {
            viewSpace1.visibility = INVISIBLE
            viewSpace2.visibility = INVISIBLE
            heightValuePicker.visibility = INVISIBLE
            postInvalidate()
        } else {
            viewSpace1.visibility = VISIBLE
            viewSpace2.visibility = VISIBLE
            heightValuePicker.visibility = VISIBLE
            postInvalidate()
        }
    }

}