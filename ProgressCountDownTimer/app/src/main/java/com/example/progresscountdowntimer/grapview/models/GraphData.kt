package com.example.progresscountdowntimer.grapview.models

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

class GraphData private constructor(builder: Builder) {
    var ctxWeakRef: WeakReference<Context?>? = null
    var graphDataPoints: PointMap? = null
    var isStraightLine = false
    val pointRadius: Int
    var isAnimateLine = false

    @ColorInt
    var strokeColor = 0

    @ColorInt
    var pointColor = 0

    @ColorInt
    var gradientStartColor = 0

    @ColorInt
    var gradientEndColor = 0
    var maxValue = 0

    class Builder : IGraphData, IGraphStroke {
        var ctxWeakRef: WeakReference<Context>? = null
        var graphDataPoints: PointMap? = null
        var isStraightLine = false
        var animateLine = false
        var pointRadius = 4

        @ColorInt
        var strokeColor = 0

        @ColorInt
        var pointColor = 0

        @ColorInt
        var gradientStartColor = 0

        @ColorInt
        var gradientEndColor = 0

        private constructor() {}
        constructor(context: Context) {
            ctxWeakRef = WeakReference(context)
        }

        override fun setPointMap(graphPoints: PointMap?): IGraphStroke {
            graphDataPoints = graphPoints
            return this
        }

        override fun setGraphStroke(colorRes: Int): Builder {
            strokeColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, colorRes)
            return this
        }

        fun setGraphGradient(start: Int, end: Int): Builder {
            gradientEndColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, end)
            gradientStartColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, start)
            return this
        }

        fun setPointColor(color: Int): Builder {
            pointColor = ContextCompat.getColor(ctxWeakRef!!.get()!!, color)
            return this
        }

        fun animateLine(animate: Boolean): Builder {
            animateLine = animate
            return this
        }

        fun setStraightLine(isStraightLine: Boolean): Builder {
            this.isStraightLine = isStraightLine
            return this
        }

        fun setPointRadius(radius: Int): Builder {
            pointRadius = radius
            return this
        }

        fun build(): GraphData {
            return GraphData(this)
        }
    }

    interface IGraphData {
        fun setPointMap(graphPoints: PointMap?): IGraphStroke
    }

    interface IGraphStroke {
        fun setGraphStroke(colorRes: Int): Builder
    }

    companion object {
        fun builder(context: Context): IGraphData {
            return Builder(context)
        }
    }

    init {
        if (builder.graphDataPoints != null) {
            graphDataPoints = builder.graphDataPoints
        } else {
            graphDataPoints = PointMap()
        }
        if (builder.ctxWeakRef != null) {
            ctxWeakRef = WeakReference(builder.ctxWeakRef!!.get())
        }
        strokeColor = builder.strokeColor
        gradientEndColor = builder.gradientEndColor
        gradientStartColor = builder.gradientStartColor
        isStraightLine = builder.isStraightLine
        pointRadius = builder.pointRadius
        if (builder.pointColor == 0) {
            pointColor = strokeColor
        } else {
            pointColor = builder.pointColor
        }
        isAnimateLine = builder.animateLine
    }
}