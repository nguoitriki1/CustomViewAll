package com.example.progresscountdowntimer.grapview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.broooapps.graphview.CurveGraphConfig
import com.example.progresscountdowntimer.grapview.models.GraphData
import com.example.progresscountdowntimer.grapview.models.GraphPoint
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Swapnil Tiwari on 2019-08-07.
 * swapniltiwari775@gmail.com
 */
class CurveGraphView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) : View(
    context, attrs, defStyleAttr
) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    var df = DecimalFormat("#######.##")

    // Builder fields
    private var verticalGuidelineCount = 0
    private var intervalCount = 0
    private var horizontalGuidelineCount = 0
    private var graphDataArray = arrayOf<GraphData>()
    var viewHeight = 0
    var viewWidth = 0
    var graphHeight = 0
    var graphWidth = 0
    var graphPadding = 32
    private val vPath = Path()
    private val hPath = Path()
    private var xAxisScalePaint: Paint = Paint()
    private var yAxisScalePaint: Paint = Paint()
    private var axisLinePaint: Paint = Paint()
    private var graphPointPaint: Paint = Paint()
    private var graphStrokePaint: Paint = Paint()
    private var graphGradientPaint: Paint = Paint()
    private var guidelinePaint: Paint = Paint()
    private var xAxis: RectF = RectF(0f, 0f, 0f, 0f)
    private var yAxis: RectF = RectF(0f, 0f, 0f, 0f)
    var xSpan = 0
    private var maxVal = 0f
    private var noDataMsg: String = ""

    var pathArrayList: ArrayList<Path> = ArrayList()
    var length: FloatArray = floatArrayOf()
    var graphStrokePaintsList: ArrayList<Paint> = ArrayList()
    var graphGradientPaintsList: ArrayList<Paint?> = ArrayList()
    var graphPointsList: ArrayList<ArrayList<GraphPoint>> = ArrayList()
    var graphPointPaintsList: ArrayList<Paint> = ArrayList()
    var valueAnimator: ValueAnimator? = null
    var ob: ObjectAnimator? = null
    private val builder: CurveGraphConfig.Builder? = null
    private var isConfigured = false


    private var animationDuration: Long = 0
    private fun initialize() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun configure(builder: CurveGraphConfig) {
        isConfigured = true
        noDataMsg = builder.noDataMsg
        verticalGuidelineCount = builder.guidelineCount
        horizontalGuidelineCount = builder.horizontalGuidelineCount
        intervalCount = builder.intervalCount
        xAxisScalePaint.textSize = 28f
        xAxisScalePaint.isAntiAlias = true
        xAxisScalePaint.color = builder.xAxisScaleColor
        xAxisScalePaint.style = Paint.Style.FILL
        yAxisScalePaint.textSize = 28f
        yAxisScalePaint.isAntiAlias = true
        yAxisScalePaint.color = builder.yAxisScaleColor
        yAxisScalePaint.style = Paint.Style.FILL
        graphPointPaint.isAntiAlias = true
        graphPointPaint.style = Paint.Style.FILL
        axisLinePaint.isAntiAlias = true
        axisLinePaint.color = builder.axisColor
        axisLinePaint.style = Paint.Style.FILL
        guidelinePaint.strokeWidth = 2f
        guidelinePaint.isAntiAlias = true
        guidelinePaint.color = builder.guidelineColor
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 0f)
        graphGradientPaint.style = Paint.Style.FILL
        graphGradientPaint = Paint(Paint.FILTER_BITMAP_FLAG)
        graphGradientPaint.isAntiAlias = true
        graphStrokePaint.strokeWidth = 2f
        graphStrokePaint.isAntiAlias = true
        graphStrokePaint.style = Paint.Style.STROKE
        animationDuration = builder.animationDuration
    }

    fun setData(span: Int, maxVal: Int, vararg graphDataArray: GraphData) {
        this.maxVal = maxVal.toFloat()
        xSpan = span
        this.graphDataArray = graphDataArray as Array<GraphData>
        graphGradientPaintsList.clear()
        graphStrokePaintsList.clear()
        graphPointPaintsList.clear()
        graphPointsList.clear()
        pathArrayList = constructPaths()
        valueAnimator?.cancel()
        ob?.cancel()
        length = lengths
        ob = ObjectAnimator.ofFloat(this, "phase", 1f, 0f)
        ob?.setDuration(animationDuration)
        ob?.setInterpolator(AccelerateInterpolator())
        ob?.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ob?.setAutoCancel(true)
        }
        ob?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                startGradientAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
                for (i in graphGradientPaintsList.indices) {
                    if (graphDataArray[i].isAnimateLine) {
                        graphGradientPaintsList[i]?.let {
                            it.alpha = 0
                            graphPointPaintsList[i].alpha = 0
                        }
                    }
                }
                invalidate()
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawAxis(canvas)
        if (!(graphDataArray.isEmpty() || noDataInGraph())) {
            drawVertGuideline(canvas)
            drawHorizontalGuidelines(canvas)
        }
        drawScaleText(canvas)
        drawInterval(canvas)
        drawGraphPaths(canvas)
        drawGraphPoints(canvas)
        drawGradients(canvas)
    }

    private fun drawAxis(canvas: Canvas) {
        canvas.drawRect(xAxis, axisLinePaint)
        canvas.drawRect(yAxis, axisLinePaint)
    }

    private fun drawInterval(canvas: Canvas) {
        if (intervalCount == 0 || graphDataArray.size == 0) return
        for (i in 1..intervalCount) {
            val msg = df.format((i * (xSpan.toFloat() / intervalCount)).toDouble())
            val xPos = i * (graphWidth - graphPadding * 2) / (intervalCount + 1)
            val yPos = (viewHeight - xAxisScalePaint.textSize).toInt()
            canvas.drawText(msg, xPos.toFloat(), yPos.toFloat(), xAxisScalePaint)
        }
    }

    private fun drawVertGuideline(canvas: Canvas) {
        if (verticalGuidelineCount == 0) return
        for (i in 1..verticalGuidelineCount) {
            vPath.reset()
            val xPos = i * (graphWidth - graphPadding * 2) / (verticalGuidelineCount + 1)
            vPath.moveTo(xPos.toFloat(), graphPadding.toFloat())
            vPath.lineTo(xPos.toFloat(), yAxis.top)
            canvas.drawPath(vPath, guidelinePaint)
        }
    }

    private fun drawHorizontalGuidelines(canvas: Canvas) {
        if (horizontalGuidelineCount == 0) return
        for (i in 1..horizontalGuidelineCount) {
            hPath.reset()
            val yPos = (i * graphHeight - graphPadding) / (horizontalGuidelineCount + 1)
            hPath.moveTo(graphPadding.toFloat(), yPos.toFloat())
            hPath.lineTo((graphWidth - graphPadding).toFloat(), yPos.toFloat())
            canvas.drawPath(hPath, guidelinePaint)
        }
    }

    private fun noDataInGraph(): Boolean {
        for (i in graphDataArray.indices) {
            val gd = graphDataArray[i].graphDataPoints
            if (gd != null){
                if (gd.pointMap.isEmpty()) continue
            }
            return false
        }
        return true
    }

    private fun startGradientAnimation() {
        valueAnimator = ValueAnimator()
        val alphaFactor = PropertyValuesHolder.ofInt("PROPERTY_ALPHA", 0, 255)
        valueAnimator?.setValues(alphaFactor)
        valueAnimator?.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator?.duration = 200
        valueAnimator?.addUpdateListener { animation ->
            for (i in graphGradientPaintsList.indices) {
                if (graphDataArray[i].isAnimateLine) {
                    if (graphGradientPaintsList[i] != null) graphGradientPaintsList[i]?.alpha =
                        (animation.getAnimatedValue("PROPERTY_ALPHA") as Int)
                    graphPointPaintsList[i].alpha =
                        (animation.getAnimatedValue("PROPERTY_ALPHA") as Int)
                }
            }
            invalidate()
        }
        valueAnimator?.start()
    }

    private val lengths: FloatArray
        get() {
            val array = FloatArray(pathArrayList.size)
            var itr = 0
            for (p in pathArrayList) {
                array[itr++] = PathMeasure(p, false).length
            }
            return array
        }

    private fun drawGraphPoints(canvas: Canvas) {
        for (i in graphPointsList.indices) {
            for (gp in graphPointsList[i]) {
                val p = graphPointPaintsList[i]
                p.style = Paint.Style.FILL
                canvas.drawCircle(gp.x, gp.y, graphDataArray[i].pointRadius.toFloat(), p)
                p.style = Paint.Style.STROKE
            }
        }
    }

    private fun constructPaths(): ArrayList<Path> {
        val morphedGraphHeight = yAxis.top.toInt() - 12
        var f1: Float
        var f2: Float
        var f4: Float
        val scaleFactor = maxVal / (morphedGraphHeight - graphPadding)
        val pathList = ArrayList<Path>()
        for (i in graphDataArray.indices) {
            val graphData = graphDataArray[i]
            updateStyleForGraphData(i, graphData)
            val pointMap = graphData.graphDataPoints
            var prevDataPoint = GraphPoint(graphPadding, morphedGraphHeight)
            val path = Path()
            path.moveTo(graphPadding.toFloat(), morphedGraphHeight.toFloat())
            val lastXPoint = (graphWidth - graphPadding * 2).toFloat()
            val gpList = ArrayList<GraphPoint>()
            for (spanIndex in 0..xSpan) {
                pointMap?.get(spanIndex)?.let {
                    val graphPoint = it
                    if (graphPoint.spanPos == 0) {
                        path.lineTo(
                            graphPadding.toFloat(),
                            morphedGraphHeight - graphPoint.value / scaleFactor
                        )
                        graphPoint.x = graphPadding.toFloat()
                    } else {
                        graphPoint.x =
                            (graphPoint.spanPos * (graphWidth - graphPadding * 2) / xSpan).toFloat()
                    }
                    if (scaleFactor > 0) {
                        graphPoint.y = morphedGraphHeight - graphPoint.value / scaleFactor
                    } else {
                        graphPoint.y = morphedGraphHeight.toFloat()
                    }
                    if (graphData.isStraightLine) {
                        path.lineTo(graphPoint.x, graphPoint.y)
                    } else {
                        if (spanIndex > 0) {
                            f1 = (prevDataPoint.x + graphPoint.x) / 2
                            f2 = prevDataPoint.y
                            f4 = graphPoint.y
                            path.cubicTo(f1, f2, f1, f4, graphPoint.x, graphPoint.y)
                        }
                    }
                    prevDataPoint = graphPoint
                    if (graphPoint.y.toInt() != morphedGraphHeight) {
                        gpList.add(graphPoint)
                    }
                }
            }
            graphPointsList.add(gpList)
            path.lineTo(lastXPoint, morphedGraphHeight.toFloat())
            path.lineTo(lastXPoint, yAxis.top)
            path.lineTo(graphPadding.toFloat(), yAxis.top)
            path.close()
            pathList.add(path)
        }
        return pathList
    }

    private fun drawScaleText(canvas: Canvas) {
        if (maxVal > 0) {
            for (i in 5 downTo 1) {
                val value = maxVal * i / 5
                val y =
                    (graphHeight - 32) * (5 - i) / 5f + graphPadding + yAxisScalePaint.textSize / 2
                canvas.drawText(
                    value.toString(),
                    (graphWidth - graphPadding + 8).toFloat(),
                    y,
                    yAxisScalePaint
                )
            }
        } else {
            canvas.drawText(
                noDataMsg,
                ((graphWidth - graphPadding * 2) / 2).toFloat(),
                ((graphHeight + graphPadding * 2) / 2).toFloat(),
                yAxisScalePaint
            )
        }
    }

    private fun drawGradients(canvas: Canvas) {
        for (i in pathArrayList.indices) {
            if (graphGradientPaintsList[i] != null) {
                canvas.drawPath(pathArrayList[i], graphGradientPaintsList[i]!!)
            }
        }
    }

    private fun drawGraphPaths(canvas: Canvas) {
        for (i in pathArrayList.indices) {
            canvas.drawPath(pathArrayList[i], graphStrokePaintsList[i])
        }
    }

    private fun updateStyleForGraphData(pos: Int, graphData: GraphData) {
        val pointPaint = Paint()
        pointPaint.style = Paint.Style.FILL_AND_STROKE
        pointPaint.color = graphData.pointColor
        graphPointPaintsList.add(pointPaint)
        val strokePaint = Paint()
        strokePaint.strokeWidth = 2f
        strokePaint.isAntiAlias = true
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = graphData.strokeColor
        graphStrokePaintsList.add(strokePaint)
        if (graphData.gradientStartColor != 0) {
            var gradientPaint = Paint()
            gradientPaint.style = Paint.Style.FILL
            gradientPaint = Paint(Paint.FILTER_BITMAP_FLAG)
            gradientPaint.isAntiAlias = true
            if (graphDataArray[pos].isAnimateLine) {
                pointPaint.alpha = 0
                gradientPaint.alpha = 0
            }
            gradientPaint.shader = LinearGradient(
                0f, 0f, 0f, graphHeight.toFloat(),
                graphData.gradientStartColor,
                graphData.gradientEndColor, Shader.TileMode.MIRROR
            )
            graphGradientPaintsList.add(gradientPaint)
        } else {
            graphGradientPaintsList.add(null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewHeight = measuredHeight - paddingTop - paddingTop
        viewWidth = measuredWidth - paddingLeft - paddingRight
        graphHeight = viewHeight - graphPadding
        graphWidth = viewWidth - graphPadding
        setMeasuredDimension(viewWidth, viewHeight)
        xAxis.left = (graphPadding - 4).toFloat()
        xAxis.top = (graphPadding - 4).toFloat()
        xAxis.right = graphPadding.toFloat()
        xAxis.bottom = graphHeight.toFloat()
        yAxis.left = graphPadding.toFloat()
        yAxis.top = graphHeight - yAxisScalePaint.textSize
        yAxis.right = (graphWidth - graphPadding).toFloat()
        yAxis.bottom = graphHeight - yAxisScalePaint.textSize + 4
    }

    fun setPhase(phase: Float) {
        for (i in pathArrayList.indices) {
            if (graphDataArray[i].isAnimateLine) {
                graphStrokePaintsList[i].pathEffect =
                    createPathEffect(length[i], phase, 0f)
            }
        }
        invalidate()
    }

    companion object {
        private fun createPathEffect(pathLength: Float, phase: Float, offset: Float): PathEffect {
            return DashPathEffect(
                floatArrayOf(pathLength, pathLength),
                Math.max(phase * pathLength, .0f)
            )
        }
    }

    init {
        initialize()
        configure(CurveGraphConfig.Builder(getContext()).build())
    }
}