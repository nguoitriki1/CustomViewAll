package com.example.progresscountdowntimer.grapview

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import com.example.progresscountdowntimer.R
import com.example.progresscountdowntimer.grapview.models.GraphPoint
import kotlin.math.roundToInt

data class GraphHValueModel(val pivotY: Float, val valueText: String)
data class GraphVValueModel(val id: Float, val pivotX: Float, val valueText: String)
data class GraphValueInput(val x: Float, val y: Float)
data class GraphPointMap(val pivotX: Float, val pivotY: Float)

class SingleGraphView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    private var breakValueString: Int = 10
    private var rangeMarginValue: Int = 10
    private var radiusSelected: Float = 10f
    private var graphPointMapSelected: GraphPointMap? = null
    private val gradientEndColor: Int = Color.WHITE
    private val gradientStartColor: Int = Color.YELLOW
    private var linePathStrokeWidth: Float = 2f
    private var lineSelectedStrokeWidth: Float = 4f
    private var xLeftGuideline: Float = 0f
    private var marginTopView: Int
    private var marginBottomView: Int
    private var marginLeftView: Int
    private var maxValue = 350
    private var rangeBreak: Int = 7
    private val viewTopLeftRect = Rect()
    private var viewTopRightRect = Rect()
    private var viewBottomRightRect = Rect()
    private var viewBottomLeftRect = Rect()
    private val listValueH = ArrayList<GraphHValueModel>()
    private val listValueV = ArrayList<GraphVValueModel>()
    private val listValueInput = ArrayList<GraphValueInput>()
    private val paintTextTop = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintTextMonthName = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintTextBottom = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLineBottom = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLinePath = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCurrentXSelected = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val linePath = Path()
    private val linePathGradient = Path()
    private var sizeTextTop = 14f
    private var sizeTextBottom = 14f
    private var sizeTextBottomTitle = 14f
    private var colorTextTop: Int
    private var colorTextBottom: Int
    private var bottomViewHeight: Int
    private var colorTextBottomTitle: Int
    private var colorTextLinePath: Int
    private var colorStrokeSelected: Int
    private var dayOfMonth = 30
    private var nameMonth = "Thang 2"


    private var horizontalGuidelineCount = 0
    private val hPath = Path()
    private var guidelinePaint: Paint = Paint()
    private var graphWidth: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)


    init {
        val defaultPadding = convertDpToPixel(16, context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.SingleGraphView)
        marginTopView = a.getInt(R.styleable.SingleGraphView_graph_padding_top, 0)
        if (marginTopView == 0) {
            marginTopView = defaultPadding.roundToInt()
        } else {
            marginTopView += defaultPadding.roundToInt()
        }
        marginBottomView = convertDpToPixel(
            a.getInt(R.styleable.SingleGraphView_graph_padding_bottom, 0),
            context
        ).toInt()
        maxValue = a.getInt(R.styleable.SingleGraphView_graph_max_value_ranger, 350)
        rangeBreak = a.getInt(R.styleable.SingleGraphView_graph_ranger_break, 7)
        sizeTextTop = a.getFloat(R.styleable.SingleGraphView_graph_ranger_text_size_top, 14f)
        sizeTextBottom = a.getFloat(R.styleable.SingleGraphView_graph_ranger_text_size_bottom, 14f)
        linePathStrokeWidth =
            a.getFloat(R.styleable.SingleGraphView_graph_line_path_stroke_width, 2f)
        lineSelectedStrokeWidth =
            a.getFloat(R.styleable.SingleGraphView_graph_selected_stroke_width, 4f)
        sizeTextBottomTitle =
            a.getFloat(R.styleable.SingleGraphView_graph_text_size_bottom_title, 12f)
        colorTextTop =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_top, Color.BLUE)
        colorTextBottom =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_bottom, Color.BLUE)
        colorTextBottomTitle =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_bottom_title, Color.RED)
        colorTextLinePath =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_line_path, Color.RED)
        colorStrokeSelected =
            a.getColor(R.styleable.SingleGraphView_graph_selected_stroke_color, Color.BLUE)
        val bottomHeight = a.getInt(R.styleable.SingleGraphView_graph_ranger_bottom_height, 0)
        a.recycle()
        bottomViewHeight = convertDpToPixel(bottomHeight, context).roundToInt()
        marginLeftView = convertDpToPixel(8, context).toInt()
        paintTextTop.textSize = convertDpToPixel(sizeTextTop.roundToInt(), context)
        paintTextTop.color = colorTextTop

        paintTextBottom.textSize = convertDpToPixel(sizeTextBottom.roundToInt(), context)
        paintTextBottom.color = colorTextBottom

        paintTextMonthName.color = colorTextBottomTitle
        paintTextMonthName.textSize = convertDpToPixel(sizeTextBottomTitle.roundToInt(), context)
        paintTextMonthName.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paintTextMonthName.textAlign = Paint.Align.CENTER

        paintLinePath.color = colorTextLinePath
        paintLinePath.style = Paint.Style.STROKE
        paintLinePath.strokeWidth = linePathStrokeWidth

        paintCurrentXSelected.color = colorStrokeSelected
        paintCurrentXSelected.style = Paint.Style.STROKE
        paintCurrentXSelected.strokeWidth = lineSelectedStrokeWidth

        listValueInput.add(GraphValueInput(1f, 250f))
        listValueInput.add(GraphValueInput(2f, 152f))
        listValueInput.add(GraphValueInput(3f, 350f))
        listValueInput.add(GraphValueInput(4f, 0f))
        listValueInput.add(GraphValueInput(5f, 90f))
        listValueInput.add(GraphValueInput(6f, 50f))
        listValueInput.add(GraphValueInput(7f, 299f))
        listValueInput.add(GraphValueInput(8f, 250f))
    }

    fun setMarginTopView(dpMargin: Int) {
        val convertDpToPixel = convertDpToPixel(dpMargin, context)
        this.marginTopView = convertDpToPixel.roundToInt()
        invalidate()
    }

    fun setMarginBottomView(dpMargin: Int) {
        val convertDpToPixel = convertDpToPixel(dpMargin, context)
        this.marginBottomView = convertDpToPixel.roundToInt()
        invalidate()
    }

    fun setMaxValue(maxRange: Int, rangeBreak: Int) {
        this.maxValue = maxRange
        this.rangeBreak = rangeBreak
        initValueData(width, height)
        invalidate()
    }

    fun setSizeText(size: Int) {
        this.sizeTextTop = size.toFloat()
        paintTextTop.textSize = convertDpToPixel(size, context)
        invalidate()
    }

    fun setTextColor(color: Int) {
        this.colorTextTop = color
        paintTextTop.color = color
        invalidate()
    }

    fun setCurrentSelected(x: Float) {
        val firstOrNull = listValueInput.firstOrNull { x == it.x }
        firstOrNull?.let {
            graphPointMapSelected = createPoint(it)
            invalidate()
        }
    }

    private fun setGuidelineCount() {
        horizontalGuidelineCount = if (listValueH.isNotEmpty()) {
            listValueH.size
        } else {
            0
        }
    }

    private fun convertDpToPixel(dp: Int, context: Context): Float {
        if (dp == 0) {
            return 0f
        }
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun convertPixelsToDp(px: Float, context: Context): Float {
        if (px == 0f) {
            return 0f
        }
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        guidelinePaint.strokeWidth = 1f
        guidelinePaint.isAntiAlias = true
        guidelinePaint.color = Color.DKGRAY
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 1f)
        graphWidth = w
        paintLineBottom.strokeWidth = 3f
        paintLineBottom.isAntiAlias = true
        paintLineBottom.color = Color.RED
        paintLineBottom.style = Paint.Style.FILL
        if (bottomViewHeight == 0)
            bottomViewHeight = (h / 10f * 1.5f).roundToInt()
        initValueData(w, h)
    }

    private fun initValueData(w: Int, h: Int) {
        listValueH.clear()
        listValueV.clear()
        viewTopLeftRect.set(
            0 + marginLeftView,
            0 + marginTopView,
            bottomViewHeight,
            h - bottomViewHeight
        )
        viewTopRightRect.set(bottomViewHeight, 0 + marginTopView, w, h - bottomViewHeight)
        viewBottomRightRect.set(bottomViewHeight, viewTopRightRect.bottom, w, h)
        viewBottomLeftRect.set(0, viewTopRightRect.bottom, bottomViewHeight, h)
        xLeftGuideline = viewTopRightRect.left.toFloat()
        if (viewTopRightRect.top < viewTopRightRect.bottom) {
            if (maxValue > 0) {
                var valueString = maxValue
                rangeMarginValue = viewTopRightRect.height() / rangeBreak
                var breakHValue = viewTopRightRect.top.toFloat()
                var breakVValue = viewBottomRightRect.left.toFloat()
                breakValueString = maxValue / rangeBreak
                for (i in 0..rangeBreak) {
                    listValueH.add(GraphHValueModel(breakHValue, valueString.toString()))
                    breakHValue += rangeMarginValue
                    valueString -= breakValueString
                }

                for (i in 1..dayOfMonth) {
                    listValueV.add(GraphVValueModel(i.toFloat(), breakVValue, i.toString()))
                    breakVValue += rangeMarginValue
                }
                radiusSelected = rangeMarginValue / 4f
            }
        }

        setupGradientPaint()
        setGuidelineCount()
        setupLinePath()
        setCurrentSelected(4f)
    }

    private fun setupGradientPaint() {
        gradientPaint.style = Paint.Style.FILL
        gradientPaint.isAntiAlias = true
        gradientPaint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            gradientStartColor,
            gradientEndColor, Shader.TileMode.MIRROR
        )

    }

    private fun createPoint(point: GraphValueInput): GraphPointMap? {
        val xPoint = listValueV.firstOrNull { point.x == it.id }?.pivotX
        val yPoint = convertValue(
            point.y,
            350f,
            0f,
            viewTopRightRect.top.toFloat(),
            viewTopRightRect.bottom.toFloat()
        )

        xPoint?.let {
            return GraphPointMap(xPoint, yPoint)
        }
        return null
    }

//    private fun constructPaths(): ArrayList<Path> {
//        val morphedGraphHeight = yAxis.top.toInt()
//        var f1: Float
//        var f2: Float
//        var f4: Float
//        val scaleFactor = maxVal / (morphedGraphHeight - graphPadding)
//        val pathList = ArrayList<Path>()
//        for (i in graphDataArray.indices) {
//            val graphData = graphDataArray[i]
//            updateStyleForGraphData(i, graphData)
//            val pointMap = graphData.graphDataPoints
//            var prevDataPoint = GraphPoint(graphPadding, morphedGraphHeight)
//            val path = Path()
//            path.moveTo(graphPadding.toFloat(), morphedGraphHeight.toFloat())
//            val lastXPoint = (graphWidth - graphPadding * 2).toFloat()
//            val gpList = ArrayList<GraphPoint>()
//            for (spanIndex in 0..xSpan) {
//                pointMap?.get(spanIndex)?.let {
//                    val graphPoint = it
//                    if (graphPoint.spanPos == 0) {
//                        path.lineTo(
//                            graphPadding.toFloat(),
//                            morphedGraphHeight - graphPoint.value / scaleFactor
//                        )
//                        graphPoint.x = graphPadding.toFloat()
//                    } else {
//                        graphPoint.x =
//                            (graphPoint.spanPos * (graphWidth - graphPadding * 2) / xSpan).toFloat()
//                    }
//                    if (scaleFactor > 0) {
//                        graphPoint.y = morphedGraphHeight - graphPoint.value / scaleFactor
//                    } else {
//                        graphPoint.y = morphedGraphHeight.toFloat()
//                    }
//                    if (graphData.isStraightLine) {
//                        path.lineTo(graphPoint.x, graphPoint.y)
//                    } else {
//                        if (spanIndex > 0) {
//                            f1 = (prevDataPoint.x + graphPoint.x) / 2
//                            f2 = prevDataPoint.y
//                            f4 = graphPoint.y
//                            path.cubicTo(f1, f2, f1, f4, graphPoint.x, graphPoint.y)
//                        }
//                    }
//                    prevDataPoint = graphPoint
//                    if (graphPoint.y.toInt() != morphedGraphHeight) {
//                        gpList.add(graphPoint)
//                    }
//                }
//            }
//            graphPointsList.add(gpList)
//            path.lineTo(lastXPoint, morphedGraphHeight.toFloat())
//            path.lineTo(lastXPoint, yAxis.top)
//            path.lineTo(graphPadding.toFloat(), yAxis.top)
//            path.close()
//            pathList.add(path)
//        }
//        return pathList
//    }

    private fun setupLinePath() {
        linePath.reset()
        linePathGradient.reset()
        if (listValueInput.size == 0) {
            return
        }
        if (listValueInput.size == 1) {
            val point = listValueInput.get(0)
            createPoint(point)?.let {
                linePath.moveTo(it.pivotX, it.pivotY)
                linePathGradient.moveTo(it.pivotX, it.pivotY)
            }
            return
        }
        if (listValueInput.size == 2) {
            val point1 = listValueInput.get(0)
            val point2 = listValueInput.get(1)
            createPoint(point1)?.let {
                linePath.moveTo(it.pivotX, it.pivotY)
                linePathGradient.moveTo(it.pivotX, it.pivotY)
                createPoint(point2)?.let {
                    linePath.lineTo(it.pivotX, it.pivotY)
                    linePathGradient.lineTo(it.pivotX, it.pivotY)
                }
            }
            return
        }

        val firstPoint = listValueInput[0]
        createPoint(firstPoint)?.let {
            linePath.moveTo(it.pivotX, it.pivotY)
            linePathGradient.moveTo(it.pivotX, it.pivotY)
            for (i in 1 until (listValueInput.size - 1)) {
                val prevPoint = createPoint(listValueInput[i - 1])
                val currPoint = createPoint(listValueInput[i])
                val nextPoint = createPoint(listValueInput[i + 1])

                if (prevPoint != null && currPoint != null && nextPoint != null) {
                    val x1 = (prevPoint.pivotX + currPoint.pivotX)/2f
                    val y1 = (prevPoint.pivotY + currPoint.pivotY)/2f

//                    val x2 = (prevPoint.pivotX + x1)/2f
//                    val y2 = (prevPoint.pivotY + y1)/2f

                    val x2 = currPoint.pivotX
                    val y2 = currPoint.pivotY

                    val x3 = (nextPoint.pivotX + currPoint.pivotX)/2f
                    val y3 = (nextPoint.pivotY + currPoint.pivotY)/2f

//                    val x5 = (x4 + currPoint.pivotX)/2f
//                    val y5 = (y4 + currPoint.pivotY)/2f

                    linePath.cubicTo(x1, y1, x2, y2, x3, y3)
//                    linePath.cubicTo(x3, y3, x4, y4, x5, y5)
                    linePathGradient.cubicTo(x1, y1, x2, y2, x3, y3)
                }
            }
            val lastPoint = createPoint(listValueInput.get(listValueInput.size - 1))
            lastPoint?.let {
                linePath.lineTo(lastPoint.pivotX, lastPoint.pivotY)
                linePathGradient.lineTo(lastPoint.pivotX, lastPoint.pivotY)
                if (lastPoint.pivotX < viewBottomRightRect.right) {
                    linePath.lineTo(viewBottomRightRect.right.toFloat(), lastPoint.pivotY)
                    linePathGradient.lineTo(viewBottomRightRect.right.toFloat(), lastPoint.pivotY)
                }
                linePathGradient.lineTo(
                    viewTopRightRect.right.toFloat(),
                    viewTopRightRect.bottom.toFloat()
                )
                linePathGradient.lineTo(
                    viewTopRightRect.left.toFloat(),
                    viewTopRightRect.bottom.toFloat()
                )
                linePathGradient.close()
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(linePath, paintLinePath)
//        canvas.drawPath(linePathGradient, gradientPaint)
        listValueH.forEach {
            canvas.drawText(it.valueText, viewTopLeftRect.left.toFloat(), it.pivotY, paintTextTop)
            drawHorizontalGuidelines(it, canvas)
        }
        listValueV.forEach {
            canvas.drawText(
                it.valueText,
                it.pivotX,
                viewBottomRightRect.centerY().toFloat(),
                paintTextBottom
            )
        }

        drawNameOfMonth(canvas)
        drawLineBottom(canvas)
        graphPointMapSelected?.let {
            canvas.drawCircle(it.pivotX, it.pivotY, radiusSelected, paintCurrentXSelected)
        }
    }

    private fun drawNameOfMonth(canvas: Canvas) {
        canvas.drawText(
            nameMonth,
            viewBottomLeftRect.centerX().toFloat(),
            viewBottomLeftRect.centerY().toFloat(),
            paintTextMonthName
        )
    }

    private fun drawLineBottom(canvas: Canvas) {
        paintLineBottom.color = Color.RED
        canvas.drawLine(
            0f,
            viewTopRightRect.bottom.toFloat(),
            width.toFloat(),
            viewTopRightRect.bottom.toFloat(),
            paintLineBottom
        )
    }

    private fun drawHorizontalGuidelines(modelH: GraphHValueModel, canvas: Canvas) {
        if (horizontalGuidelineCount > 0) {
            hPath.reset()
            val yPos = modelH.pivotY
            hPath.moveTo(xLeftGuideline, yPos)
            hPath.lineTo((graphWidth).toFloat(), yPos)
            canvas.drawPath(hPath, guidelinePaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = getWidthScreen()
        val desiredHeight = getWidthScreen()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int

        //Measure Width
        width = if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            Math.min(desiredWidth, widthSize)
        } else {
            //Be whatever you want
            desiredWidth
        }

        //Measure Height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                Math.min(desiredHeight, heightSize)
            }
            else -> {
                //Be whatever you want
                desiredHeight
            }
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    private fun getWidthScreen(): Int {
        return Resources.getSystem().getDisplayMetrics().widthPixels
    }

    private fun convertValue(
        currValue: Float,
        min1: Float,
        max1: Float,
        min2: Float,
        max2: Float
    ): Float {
        return (currValue - min1) / (max1 - min1) * (max2 - min2) + min2
    }


}