package com.example.progresscountdowntimer.grapview

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.progresscountdowntimer.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


data class GraphHValueModel(val pivotY: Float, val valueText: String)
data class GraphVValueModel(val id: Float, val pivotX: Float, val valueText: String)
data class GraphValueInput(val x: Float, val y: Float)
data class GraphPointMap(val pivotX: Float, val pivotY: Float)


class SingleGraphView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    private var mTranslateX: Float = 0f
    private var valueText: String = ""
    private var breakValueString: Int = 10
    private var rangeMarginValue: Int = 10
    private var radiusSelected: Float = 10f
    private var graphPointMapSelected: GraphPointMap? = null
    private val gradientEndColor: Int = Color.WHITE
    private val gradientStartColor: Int = Color.YELLOW
    private var linePathStrokeWidth: Float = 14f
    private var lineSelectedStrokeWidth: Float = 10f
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
    private val paintTextValueSelected = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintTextBottom = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLineBottom = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLinePath = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCurrentXSelected = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circleSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var guidelinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val backgroundColorPaint = Paint()
    private val linePath = Path()
    private val linePathGradient = Path()
    private var sizeTextTop = 14f
    private var sizeTextBottom = 14f
    private var sizeTextBottomTitle = 16f
    private var colorTextTop: Int
    private var colorTextBottom: Int
    private var bottomViewHeight: Int
    private var colorTextBottomTitle: Int
    private var colorTextValueSelected: Int
    private var colorTextLinePath: Int
    private var colorBackground: Int
    private var colorStrokeSelected: Int
    private var dayOfMonth = 30
    private var nameMonth = "April"
    private var marginTopViewToBottomView = 20f
    private var mFlingVelocity = 0f

    private var horizontalGuidelineCount = 0
    private val hPath = Path()
    private var maxDrawWidth: Int = 0
    private var defaultPadding: Float = 0f
    private var gestureDetector: GestureDetector? = null

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)


    init {
        defaultPadding = convertDpToPixel(8, context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.SingleGraphView)
        marginTopView = a.getInt(R.styleable.SingleGraphView_graph_padding_top, 32)
        marginTopView = if (marginTopView == 0) {
            defaultPadding.roundToInt()
        } else {
            val marginTop = convertDpToPixel(marginTopView, context)
            (marginTop + defaultPadding).roundToInt()
        }
        marginTopViewToBottomView = convertDpToPixel(20, context)
        marginBottomView = convertDpToPixel(
            a.getInt(R.styleable.SingleGraphView_graph_padding_bottom, 0),
            context
        ).toInt()
        maxValue = a.getInt(R.styleable.SingleGraphView_graph_max_value_ranger, 350)
        rangeBreak = a.getInt(R.styleable.SingleGraphView_graph_ranger_break, 7)
        sizeTextTop = a.getFloat(R.styleable.SingleGraphView_graph_ranger_text_size_top, 14f)
        sizeTextBottom = a.getFloat(R.styleable.SingleGraphView_graph_ranger_text_size_bottom, 16f)
        linePathStrokeWidth =
            a.getFloat(R.styleable.SingleGraphView_graph_line_path_stroke_width, 14f)
        lineSelectedStrokeWidth =
            a.getFloat(R.styleable.SingleGraphView_graph_selected_stroke_width, 10f)
        sizeTextBottomTitle =
            a.getFloat(R.styleable.SingleGraphView_graph_text_size_bottom_title, 12f)
        colorTextTop =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_top,ContextCompat.getColor(context,R.color.colorTextGray))
        colorTextBottom =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_bottom, ContextCompat.getColor(context,R.color.colorTextBlack))
        colorTextBottomTitle =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_bottom_title, ContextCompat.getColor(context,R.color.colorTextBlack))
        colorTextValueSelected =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_value_selected, Color.RED)
        colorTextLinePath =
            a.getColor(R.styleable.SingleGraphView_graph_text_color_line_path, ContextCompat.getColor(context,R.color.colorOrange))
        colorBackground =
            a.getColor(R.styleable.SingleGraphView_graph_background_color, Color.WHITE)
        colorStrokeSelected =
            a.getColor(
                R.styleable.SingleGraphView_graph_selected_stroke_color, ContextCompat.getColor(
                    context,
                    R.color.colorGraphStrokeSelected
                )
            )
        val bottomHeight = a.getInt(R.styleable.SingleGraphView_graph_ranger_bottom_height, 0)
        a.recycle()
        bottomViewHeight = convertDpToPixel(bottomHeight, context).roundToInt()
        marginLeftView = convertDpToPixel(8, context).toInt()
        paintTextTop.textSize = convertDpToPixel(sizeTextTop.roundToInt(), context)
        paintTextTop.color = colorTextTop
        paintTextTop.textAlign = Paint.Align.CENTER

        paintTextBottom.textSize = convertDpToPixel(sizeTextBottom.roundToInt(), context)
        paintTextBottom.color = colorTextBottom
        paintTextBottom.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paintTextBottom.textAlign = Paint.Align.CENTER

        paintTextMonthName.color = colorTextBottomTitle
        paintTextMonthName.textSize = convertDpToPixel(sizeTextBottomTitle.roundToInt(), context)
        paintTextMonthName.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paintTextMonthName.textAlign = Paint.Align.CENTER

        paintTextValueSelected.color = colorTextValueSelected
        paintTextValueSelected.textSize =
            convertDpToPixel(sizeTextBottomTitle.roundToInt(), context)
        paintTextValueSelected.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paintTextValueSelected.textAlign = Paint.Align.CENTER

        paintLinePath.color = colorTextLinePath
        paintLinePath.style = Paint.Style.STROKE
        paintLinePath.strokeWidth = linePathStrokeWidth

        paintCurrentXSelected.color = colorStrokeSelected
        paintCurrentXSelected.style = Paint.Style.STROKE
        paintCurrentXSelected.strokeWidth = lineSelectedStrokeWidth


        circleSelectedPaint.color = Color.WHITE
        circleSelectedPaint.style = Paint.Style.FILL

        setBackgroundColor(colorBackground)
        backgroundColorPaint.color = colorBackground
        backgroundColorPaint.style = Paint.Style.FILL

        listValueInput.add(GraphValueInput(1f, 250f))
        listValueInput.add(GraphValueInput(2f, 152f))
        listValueInput.add(GraphValueInput(3f, 350f))
        listValueInput.add(GraphValueInput(4f, 0f))
        listValueInput.add(GraphValueInput(5f, 90f))
        listValueInput.add(GraphValueInput(6f, 50f))
        listValueInput.add(GraphValueInput(7f, 299f))
        listValueInput.add(GraphValueInput(8f, 250f))

        gestureDetector = GestureDetector(context, GestureListener())
        isClickable = true
        isFocusable = true
        setOnTouchListener { v, event ->
            gestureDetector?.let {
                it.onTouchEvent(event)
            }
            true
        }
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
            valueText = it.y.roundToInt().toString()
            graphPointMapSelected = createPoint(it)
            invalidate()
        }
    }

    fun setBottomTitle(title : String){
        nameMonth = title
        invalidate()
    }

    fun removeCurrentSelected(){
        graphPointMapSelected = null
        invalidate()
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
        guidelinePaint.strokeWidth = 4f
        guidelinePaint.isAntiAlias = true
        guidelinePaint.color = ContextCompat.getColor(context, R.color.dashColor)
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(3f, 6f), 1f)
        maxDrawWidth = w

        paintLineBottom.strokeWidth = 3f
        paintLineBottom.isAntiAlias = true
        paintLineBottom.color = colorTextTop
        paintLineBottom.style = Paint.Style.FILL
        if (bottomViewHeight == 0)
            bottomViewHeight = (h / 10f * 1.5f).roundToInt()
        initValueData(w, h)
    }

    private fun initValueData(w: Int, h: Int) {
        listValueH.clear()
        listValueV.clear()
        val bottomViewTop = (h - bottomViewHeight - marginTopViewToBottomView).roundToInt()
        val lefViewRight = (bottomViewHeight + defaultPadding).toInt()
        viewTopLeftRect.set(
            0,
            0,
            bottomViewHeight,
            h - bottomViewHeight
        )
        viewTopRightRect.set(lefViewRight, 0 + marginTopView, w, bottomViewTop)

        val topViewBottom = (h - bottomViewHeight)

        viewBottomRightRect.set(lefViewRight, topViewBottom, w, h)
        viewBottomLeftRect.set(0, topViewBottom, bottomViewHeight, h)


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
                maxDrawWidth = (breakVValue + rangeMarginValue).toInt()
                radiusSelected = rangeMarginValue / 6f
            }
        }

        setupGradientPaint()
        setGuidelineCount()
        setupLinePath()
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

    private fun createPoint(point: GraphValueInput): GraphPointMap {
        val xPoint = viewTopRightRect.left + ((point.x - 1) * rangeMarginValue)
        var yPoint = convertValue(
            point.y,
            350f,
            0f,
            viewTopRightRect.top.toFloat(),
            viewTopRightRect.bottom.toFloat()
        )

        if (yPoint == viewTopRightRect.top.toFloat()){
            yPoint = (viewTopRightRect.top + (linePathStrokeWidth/2f))
        }
        if (yPoint == viewTopRightRect.bottom.toFloat()){
            yPoint = (viewTopRightRect.bottom - (linePathStrokeWidth/2f))
        }

        return GraphPointMap(xPoint, yPoint)
    }

    private fun setupLinePath() {
        linePath.reset()
        linePathGradient.reset()
        if (listValueInput.size == 0) {
            return
        }
        if (listValueInput.size == 1) {
            val point = listValueInput.get(0)
            createPoint(point).let {
                linePath.moveTo(it.pivotX, it.pivotY)
                linePathGradient.moveTo(it.pivotX, it.pivotY)
            }
            return
        }
        if (listValueInput.size == 2) {
            val point1 = listValueInput.get(0)
            val point2 = listValueInput.get(1)
            createPoint(point1).let {
                linePath.moveTo(it.pivotX, it.pivotY)
                linePathGradient.moveTo(it.pivotX, it.pivotY)
                createPoint(point2).let {
                    linePath.lineTo(it.pivotX, it.pivotY)
                    linePathGradient.lineTo(it.pivotX, it.pivotY)
                }
            }
            return
        }

        val firstPoint = listValueInput[0]
        createPoint(firstPoint).let { pointMap ->
            linePath.moveTo(pointMap.pivotX, pointMap.pivotY)
            linePathGradient.moveTo(pointMap.pivotX, pointMap.pivotY)
            for (i in 1 until (listValueInput.size)) {
                val prevPoint = createPoint(listValueInput[i - 1])
                val currPoint = createPoint(listValueInput[i])

                val f1 = (prevPoint.pivotX + currPoint.pivotX) / 2f
                val f2 = prevPoint.pivotY

                val f4 = currPoint.pivotY

                linePath.cubicTo(f1, f2, f1, f4, currPoint.pivotX, currPoint.pivotY)
                linePathGradient.cubicTo(f1, f2, f1, f4, currPoint.pivotX, currPoint.pivotY)
            }
            val lastPoint = createPoint(listValueInput[listValueInput.size - 1])
            lastPoint.let {
                linePathGradient.lineTo(
                    it.pivotX,
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
        canvas.save()
        canvas.translate(mTranslateX, 0f)
        drawLinePath(canvas)
        drawHorizontalGuidelines(canvas)
        drawSelected(canvas)
        canvas.restore()

        canvas.drawRect(viewTopLeftRect, backgroundColorPaint)

        drawRowValueNumber(canvas)

        canvas.save()
        canvas.translate(mTranslateX, 0f)
        listValueV.forEach {
            canvas.drawText(
                it.valueText,
                it.pivotX,
                viewBottomRightRect.centerY().toFloat(),
                paintTextBottom
            )
        }
        canvas.restore()
        canvas.drawRect(viewBottomLeftRect, backgroundColorPaint)
        drawNameOfMonth(canvas)
        drawLineBottom(canvas)
        checkFlingVelocity()
    }

    private fun drawRowValueNumber(canvas: Canvas) {
        listValueH.forEach {
            canvas.drawText(it.valueText, viewTopLeftRect.width() / 2f, it.pivotY, paintTextTop)
        }
    }

    private fun drawLinePath(canvas: Canvas) {
        canvas.drawPath(linePath, paintLinePath)
        canvas.drawPath(linePathGradient, gradientPaint)
    }

    private fun drawSelected(canvas: Canvas) {
        graphPointMapSelected?.let {
            canvas.drawCircle(it.pivotX, it.pivotY, radiusSelected, paintCurrentXSelected)
            canvas.drawCircle(it.pivotX, it.pivotY, radiusSelected, circleSelectedPaint)
            canvas.drawText(
                valueText,
                it.pivotX,
                it.pivotY - defaultPadding - radiusSelected,
                paintTextValueSelected
            )
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
        canvas.drawLine(
            defaultPadding,
            viewBottomRightRect.top.toFloat(),
            width.toFloat() - defaultPadding,
            viewBottomRightRect.top.toFloat(),
            paintLineBottom
        )
    }

    private fun drawHorizontalGuidelines(canvas: Canvas) {
        listValueH.forEach {
            if (horizontalGuidelineCount > 0) {
                hPath.reset()
                val yPos = it.pivotY
                hPath.moveTo(xLeftGuideline, yPos)
                hPath.lineTo((maxDrawWidth).toFloat(), yPos)
                canvas.drawPath(hPath, guidelinePaint)
            }
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

    val valueFling = 250f
    fun checkFlingVelocity() {
        Log.d("taihhh", "checkFlingVelocity: ${mFlingVelocity}")
        if (mFlingVelocity != 0f) {
            val offsetDelta = mFlingVelocity / 30f
            if (mFlingVelocity > valueFling) {
                mFlingVelocity -= valueFling
            } else if (mFlingVelocity < -valueFling) {
                mFlingVelocity += valueFling
            } else {
                mFlingVelocity = 0f
            }
            mTranslateX += offsetDelta
            if (mTranslateX < -(maxDrawWidth - width)) {
                mTranslateX = -(maxDrawWidth - width).toFloat()
                mFlingVelocity = 0f
            }
            if (mTranslateX > 0) {
                mTranslateX = 0f
                mFlingVelocity = 0f
            }
            invalidate()
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 150
        private val SWIPE_MIN_DISTANCE = 120
        private val SWIPE_MAX_OFF_PATH = 250
        private val SWIPE_THRESHOLD_VELOCITY = 200
        var x = 0f
        var y = 0f
        override fun onDown(e: MotionEvent?): Boolean {
            x = 0f;
            y = 0f;
            return true
        }




        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            x -= distanceX;
            y -= distanceY;
            if (abs(x) > abs(y)) {
                onTwoFingerSwipeRight(-distanceX)
            }
            return true;
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
                if (abs(e1.x - e2.x) > SWIPE_MAX_OFF_PATH
                    || abs(velocityY) < SWIPE_THRESHOLD_VELOCITY
                ) return false
                if (e1.y - e2.y > SWIPE_MIN_DISTANCE) onSwipeUp() else if (e2.y - e1.y > SWIPE_MIN_DISTANCE) onSwipeDown()
            } else {
                if (abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) return false
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE) onSwipeLeft(velocityX) else if (e2.x - e1.x > SWIPE_MIN_DISTANCE) onSwipeRight(velocityX)
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }



        private fun onSwipeRight(velocityX: Float) {
            mFlingVelocity = velocityX
            checkFlingVelocity()
//            mTranslateX += fl
//            mTranslateX = max(min(0f, mTranslateX), -(maxDrawWidth - width).toFloat())
//            invalidate()
        }

        private fun onSwipeLeft(velocityX: Float) {
            mFlingVelocity = velocityX
            checkFlingVelocity()
//            mTranslateX += fl
//            mTranslateX = max(min(0f, mTranslateX), -(maxDrawWidth - width).toFloat())
//            invalidate()
        }

        private fun onSwipeUp() {
            Log.d("abc", "onSwipeUp")
        }

        private fun onSwipeDown() {
            Log.d("abc", "onSwipeDown")
        }

        private fun onTwoFingerSwipeLeft(dx: Float) {
            Log.d("abc", "onTwoFingerSwipeLeft")
            mTranslateX += dx
            mTranslateX = max(min(0f, mTranslateX), -(maxDrawWidth - width).toFloat())
            invalidate()
        }

        private fun onTwoFingerSwipeRight(dx: Float) {
            Log.d("abc", "onTwoFingerSwipeRight")
            mTranslateX += dx
            mTranslateX = max(min(0f, mTranslateX), -(maxDrawWidth - width).toFloat())
            invalidate()
        }
    }

}