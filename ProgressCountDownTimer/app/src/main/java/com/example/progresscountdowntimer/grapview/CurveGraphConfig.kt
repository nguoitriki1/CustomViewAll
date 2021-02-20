package com.broooapps.graphview

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.progresscountdowntimer.R
import java.lang.ref.WeakReference

class CurveGraphConfig private constructor(builder: Builder) {
    @ColorInt
    var axisColor: Int

    @ColorInt
    var xAxisScaleColor: Int

    @ColorInt
    var guidelineColor: Int

    @ColorInt
    var yAxisScaleColor: Int
    var guidelineCount: Int
    var horizontalGuidelineCount: Int
    var intervalCount: Int
    var animationDuration: Long
    var noDataMsg: String

    class Builder {
        var ctxWeakRef: WeakReference<Context>? = null

        @ColorInt
        var axisColor = 0

        @ColorInt
        var xAxisScaleColor = 0

        @ColorInt
        var guidelineColor = 0

        @ColorInt
        var yAxisScaleColor = 0
        var guidelineCount = 0
        var intervalCount = 0
        var horizontalGuidelineCount = 0
        var animationDuration: Long = 2000
        var noDataMsg: String? = null

        private constructor() {}
        constructor(context: Context) {
            ctxWeakRef = WeakReference(context)
        }

        fun setNoDataMsg(message: String?): Builder {
            noDataMsg = message
            return this
        }

        fun setVerticalGuideline(count: Int): Builder {
            guidelineCount = count
            return this
        }

        fun setHorizontalGuideline(count: Int): Builder {
            horizontalGuidelineCount = count
            return this
        }

        fun setGuidelineColor(colorRes: Int): Builder {
            guidelineColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, colorRes)
            return this
        }

        fun setxAxisScaleTextColor(colorRes: Int): Builder {
            xAxisScaleColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, colorRes)
            return this
        }

        fun setAxisColor(colorRes: Int): Builder {
            axisColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, colorRes)
            return this
        }

        fun setyAxisScaleTextColor(colorRes: Int): Builder {
            yAxisScaleColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, colorRes)
            return this
        }

        fun setIntervalDisplayCount(intervalCount: Int): Builder {
            this.intervalCount = intervalCount
            return this
        }

        fun setAnimationDuration(duration: Long): Builder {
            animationDuration = duration
            return this
        }

        fun build(): CurveGraphConfig {
            return CurveGraphConfig(this)
        }
    }

    init {
        xAxisScaleColor = if (builder.xAxisScaleColor == 0) ContextCompat.getColor(
            builder.ctxWeakRef!!.get()!!,
            R.color.blackColor
        ) else builder.xAxisScaleColor
        yAxisScaleColor = if (builder.yAxisScaleColor == 0) ContextCompat.getColor(
            builder.ctxWeakRef!!.get()!!,
            R.color.scaleTextColor
        ) else builder.yAxisScaleColor
        guidelineColor = if (builder.guidelineColor == 0) ContextCompat.getColor(
            builder.ctxWeakRef!!.get()!!,
            R.color.guidelineColor
        ) else builder.guidelineColor
        axisColor = if (builder.axisColor == 0) ContextCompat.getColor(
            builder.ctxWeakRef!!.get()!!,
            R.color.axisColor
        ) else builder.axisColor
        intervalCount = builder.intervalCount
        guidelineCount = builder.guidelineCount
        horizontalGuidelineCount = builder.horizontalGuidelineCount
        noDataMsg = if (builder.noDataMsg == null) "NO DATA" else builder.noDataMsg!!
        animationDuration = builder.animationDuration
        builder.ctxWeakRef!!.clear()
    }
}