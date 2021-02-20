package com.example.progresscountdowntimer.pickerdate.datepicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.example.progresscountdowntimer.R
import java.util.*
import kotlin.math.max
import kotlin.math.min

abstract class WheelPicker<V> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var fontNormalTypeface: Typeface?
    private var fontSelectedTypeface: Typeface?
    private var dateHelper = DateHelper() // Overwritten from Single..Picker
    private var defaultValue: V? = null
    private var lastScrollPosition = 0
    private var listener: Listener<WheelPicker<*>, V?>? = null
    private var adapter: Adapter<V>? = Adapter()
    private var customLocale: Locale? = null
    private val paintTextSelect: Paint?
    private val paintTextNormal: Paint?
    private lateinit var scroller: Scroller
    private var tracker: VelocityTracker? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null
    private var onWheelChangeListener: OnWheelChangeListener? = null
    private val rectDrawn = Rect()
    private val rectIndicatorHead = Rect()
    private val rectIndicatorFoot = Rect()
    private val rectCurrentItem = Rect()
    private val camera = Camera()
    private val matrixRotate = Matrix()
    private val matrixDepth = Matrix()
    private var maxWidthText: String?
    private var mVisibleItemCount: Int
    private var mDrawnItemCount = 0
    private var mHalfDrawnItemCount = 0
    private var mTextMaxWidth = 0
    private var mTextMaxHeight = 0
    private var mItemTextColor: Int
    private var mSelectedItemTextColor: Int
    private var mItemTextSize: Int
    private var mIndicatorSize: Int
    private var mIndicatorColor: Int
    private var mCurtainColor: Int
    private var mItemSpace: Int
    private var mMaxAngle = MAX_ANGLE
    private var mItemAlign: Int
    private var mItemHeight = 0
    private var mHalfItemHeight = 0
    private var mHalfWheelHeight = 0
    private var selectedItemPosition: Int = 0
    private var currentItemPosition: Int = 0
    private var minFlingY = 0
    private var maxFlingY = 0
    private var minimumVelocity = 50
    private var maximumVelocity = 8000
    private var wheelCenterX = 0
    private var wheelCenterY = 0
    private var drawnCenterX = 0
    private var drawnCenterY = 0
    private var scrollOffsetY = 0
    private var textMaxWidthPosition: Int
    private var lastPointY = 0
    private var downPointY = 0
    private var touchSlop = 8
    private var hasSameWidth: Boolean
    private var hasIndicator: Boolean
    private var hasCurtain: Boolean
    private var hasAtmospheric: Boolean
    private var isCyclic: Boolean
    private var isCurved: Boolean
    private var showOnlyFutureDate = false
    private var isClick = false
    private var isForceFinishScroll = false
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (null == adapter) return
            val itemCount = adapter!!.getItemCount()
            if (itemCount == 0) return
            if (scroller.isFinished && !isForceFinishScroll) {
                if (mItemHeight == 0) return
                var position = (-scrollOffsetY / mItemHeight + selectedItemPosition) % itemCount
                position = if (position < 0) position + itemCount else position
                currentItemPosition = position
                onItemSelected()
                if (null != onWheelChangeListener) {
                    onWheelChangeListener!!.onWheelSelected(position)
                    onWheelChangeListener!!.onWheelScrollStateChanged(SCROLL_STATE_IDLE)
                }
            }
            if (scroller.computeScrollOffset()) {
                if (null != onWheelChangeListener) {
                    onWheelChangeListener!!.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING)
                }
                scrollOffsetY = scroller.currY
                val position = (-scrollOffsetY / mItemHeight + selectedItemPosition) % itemCount
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onCurrentItemOfScroll(this@WheelPicker, position)
                }
                onItemCurrentScroll(position, adapter!!.getItem(position))
                postInvalidate()
                postDelayed(this, 16)
            }
        }
    }

    fun isShowOnlyFutureDate(): Boolean {
        return showOnlyFutureDate
    }

    fun getCurved(): Boolean {
        return isCurved
    }

    fun getDateHelper(): DateHelper {
        return dateHelper
    }


    fun getCurrentItemPosition(): Int {
        return currentItemPosition
    }

    fun getAdapter(): Adapter<V>? {
        return adapter
    }

    protected abstract fun initFirst()
    protected abstract fun initDefault(): V
    fun updateAdapter() {
        adapter!!.setData(generateAdapterValues(showOnlyFutureDate)!!)
        notifyDatasetChanged()
    }

    protected abstract fun generateAdapterValues(showOnlyFutureDates: Boolean): List<V>?
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAdapter(adapter)
        defaultValue?.let {
            setDefault(it)
        }
    }

    private fun updateVisibleItemCount() {
        if (mVisibleItemCount < 2) {
            throw ArithmeticException("Wheel's visible item count can not be less than 2!")
        }
        if (mVisibleItemCount % 2 == 0) mVisibleItemCount += 1
        mDrawnItemCount = mVisibleItemCount + 2
        mHalfDrawnItemCount = mDrawnItemCount / 2
    }

    private fun computeTextSize() {
        paintTextSelect?.let { paint ->
            mTextMaxHeight = 0
            mTextMaxWidth = mTextMaxHeight
            if (hasSameWidth) {
                mTextMaxWidth = paint.measureText(adapter!!.getItemText(0)).toInt()
            } else if (isPosInRang(textMaxWidthPosition)) {
                mTextMaxWidth =
                    paint.measureText(adapter!!.getItemText(textMaxWidthPosition)).toInt()
            } else if (!TextUtils.isEmpty(maxWidthText)) {
                mTextMaxWidth = paint.measureText(maxWidthText).toInt()
            } else {
                adapter?.let {
                    val itemCount = it.getItemCount()
                    for (i in 0 until itemCount) {
                        val text = it.getItemText(i)
                        val width = paint.measureText(text).toInt()
                        mTextMaxWidth = max(mTextMaxWidth, width)
                    }
                }
            }
            val metrics = paint.fontMetrics
            mTextMaxHeight = (metrics.bottom - metrics.top).toInt()
        }

        paintTextNormal?.let { paint ->
            mTextMaxHeight = 0
            mTextMaxWidth = mTextMaxHeight
            if (hasSameWidth) {
                mTextMaxWidth = paint.measureText(adapter!!.getItemText(0)).toInt()
            } else if (isPosInRang(textMaxWidthPosition)) {
                mTextMaxWidth =
                    paint.measureText(adapter!!.getItemText(textMaxWidthPosition)).toInt()
            } else if (!TextUtils.isEmpty(maxWidthText)) {
                mTextMaxWidth = paint.measureText(maxWidthText).toInt()
            } else {
                adapter?.let {
                    val itemCount = it.getItemCount()
                    for (i in 0 until itemCount) {
                        val text = it.getItemText(i)
                        val width = paint.measureText(text).toInt()
                        mTextMaxWidth = max(mTextMaxWidth, width)
                    }
                }
            }
            val metrics = paint.fontMetrics
            mTextMaxHeight = (metrics.bottom - metrics.top).toInt()
        }

    }

    private fun updateItemTextAlign() {
        paintTextSelect?.let {
            when (mItemAlign) {
                ALIGN_LEFT -> it.textAlign = Paint.Align.LEFT
                ALIGN_RIGHT -> it.textAlign = Paint.Align.RIGHT
                else -> it.textAlign = Paint.Align.CENTER
            }
        }

        paintTextNormal?.let {
            when (mItemAlign) {
                ALIGN_LEFT -> it.textAlign = Paint.Align.LEFT
                ALIGN_RIGHT -> it.textAlign = Paint.Align.RIGHT
                else -> it.textAlign = Paint.Align.CENTER
            }
        }

    }

    private fun updateDefault() {
        defaultItemPosition?.let {
            setSelectedItemPosition(it)
        }
    }

    open fun setDefault(defaultValue: V) {
        this.defaultValue = defaultValue
        updateDefault()
    }

    fun setDefaultDate(date: Date) {
        if (adapter != null && adapter!!.getItemCount() > 0) {
            val indexOfDate = findIndexOfDate(date)
            if (indexOfDate >= 0) {
                defaultValue = adapter!!.getData()[indexOfDate]
                setSelectedItemPosition(indexOfDate)
            }
        }
    }

    fun selectDate(date: Date) {
        setSelectedItemPosition(findIndexOfDate(date))
    }

    fun setListener(listener: Listener<WheelPicker<*>, V?>) {
        this.listener = listener
    }

    open fun setCustomLocale(customLocale: Locale?) {
        this.customLocale = customLocale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)

        var resultWidth = mTextMaxWidth
        var resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1)

        if (isCurved) {
            resultHeight =
                (2 * sinDegree(mMaxAngle.toFloat()) / (Math.PI * mMaxAngle / 90f) * resultHeight).toInt()
        }

        // Consideration padding influence the view sizes
        resultWidth += paddingLeft + paddingRight
        resultHeight += paddingTop + paddingBottom

        // Consideration sizes of parent can influence the view sizes
        resultWidth = measureSize(modeWidth, sizeWidth, resultWidth)
        resultHeight = measureSize(modeHeight, sizeHeight, resultHeight)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    private fun measureSize(mode: Int, sizeExpect: Int, sizeActual: Int): Int {
        var realSize: Int
        if (mode == MeasureSpec.EXACTLY) {
            realSize = sizeExpect
        } else {
            realSize = sizeActual
            if (mode == MeasureSpec.AT_MOST) realSize = Math.min(realSize, sizeExpect)
        }
        return realSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        // Set content region
        rectDrawn[paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom

        // Get the center coordinates of content region
        wheelCenterX = rectDrawn.centerX()
        wheelCenterY = rectDrawn.centerY()

        // Correct item drawn center
        computeDrawnCenter()
        mHalfWheelHeight = rectDrawn.height() / 2
        mItemHeight = rectDrawn.height() / mVisibleItemCount
        mHalfItemHeight = mItemHeight / 2

        // Initialize fling max Y-coordinates
        computeFlingLimitY()

        // Correct region of indicator
        computeIndicatorRect()

        // Correct region of current select item
        computeCurrentItemRect()
    }

    private fun computeDrawnCenter() {
        drawnCenterX = when (mItemAlign) {
            ALIGN_LEFT -> rectDrawn.left
            ALIGN_RIGHT -> rectDrawn.right
            else -> wheelCenterX
        }
        drawnCenterY =
            (wheelCenterY - (paintTextSelect!!.ascent() + paintTextSelect.descent()) / 2).toInt()
    }

    private fun computeFlingLimitY() {
        val currentItemOffset = selectedItemPosition * mItemHeight
        minFlingY =
            if (isCyclic) Int.MIN_VALUE else -mItemHeight * (adapter!!.getItemCount() - 1) + currentItemOffset
        maxFlingY = if (isCyclic) Int.MAX_VALUE else currentItemOffset
    }

    private fun computeIndicatorRect() {
        if (!hasIndicator) return
        val halfIndicatorSize = mIndicatorSize / 2
        val indicatorHeadCenterY = wheelCenterY + mHalfItemHeight
        val indicatorFootCenterY = wheelCenterY - mHalfItemHeight
        rectIndicatorHead[rectDrawn.left, indicatorHeadCenterY - halfIndicatorSize, rectDrawn.right] =
            indicatorHeadCenterY + halfIndicatorSize
        rectIndicatorFoot[rectDrawn.left, indicatorFootCenterY - halfIndicatorSize, rectDrawn.right] =
            indicatorFootCenterY + halfIndicatorSize
    }

    private fun computeCurrentItemRect() {
        if (!hasCurtain && mSelectedItemTextColor == -1) return
        rectCurrentItem[rectDrawn.left, wheelCenterY - mHalfItemHeight, rectDrawn.right] =
            wheelCenterY + mHalfItemHeight
    }

    init {
         fontSelectedTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
         fontNormalTypeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    }

    override fun onDraw(canvas: Canvas) {
        if (null != onWheelChangeListener) onWheelChangeListener?.onWheelScrolled(scrollOffsetY)
        if (mItemHeight - mHalfDrawnItemCount <= 0) {
            return
        }
        val drawnDataStartPos = -scrollOffsetY / mItemHeight - mHalfDrawnItemCount
        var drawnDataPos = drawnDataStartPos + selectedItemPosition
        var drawnOffsetPos = -mHalfDrawnItemCount
        while (drawnDataPos < drawnDataStartPos + selectedItemPosition + mDrawnItemCount) {
            var data = ""
            if (isCyclic) {
                val itemCount = adapter?.getItemCount()
                itemCount?.let {
                    var actualPos = drawnDataPos % itemCount
                    actualPos = if (actualPos < 0) actualPos + itemCount else actualPos
                    adapter?.getItemText(actualPos)?.let {
                        data = it
                    }
                }
            } else {
                if (isPosInRang(drawnDataPos)) {
                    data = adapter!!.getItemText(drawnDataPos)
                }
            }
            paintTextSelect?.color = mItemTextColor
            paintTextSelect?.style = Paint.Style.FILL
            paintTextNormal?.color = mItemTextColor
            paintTextNormal?.style = Paint.Style.FILL
            val mDrawnItemCenterY =
                drawnCenterY + drawnOffsetPos * mItemHeight + scrollOffsetY % mItemHeight
            var distanceToCenter = 0f
            if (isCurved) {
                // Correct ratio of item's drawn center to wheel center
                val ratio = (drawnCenterY - Math.abs(drawnCenterY - mDrawnItemCenterY) -
                        rectDrawn.top) * 1.0f / (drawnCenterY - rectDrawn.top)

                // Correct unit
                var unit = 0
                if (mDrawnItemCenterY > drawnCenterY) {
                    unit = 1
                } else if (mDrawnItemCenterY < drawnCenterY) unit = -1
                val degree = clamp(
                    -(1 - ratio) * mMaxAngle * unit,
                    -mMaxAngle.toFloat(),
                    mMaxAngle.toFloat()
                )
                distanceToCenter = computeYCoordinateAtAngle(degree)
                var transX = wheelCenterX.toFloat()
                when (mItemAlign) {
                    ALIGN_LEFT -> transX = rectDrawn.left.toFloat()
                    ALIGN_RIGHT -> transX = rectDrawn.right.toFloat()
                }
                val transY = wheelCenterY - distanceToCenter
                camera.save()
                camera.rotateX(degree)
                camera.getMatrix(matrixRotate)
                camera.restore()
                matrixRotate.preTranslate(-transX, -transY)
                matrixRotate.postTranslate(transX, transY)
                camera.save()
                camera.translate(0f, 0f, computeDepth(degree.toFloat()))
                camera.getMatrix(matrixDepth)
                camera.restore()
                matrixDepth.preTranslate(-transX, -transY)
                matrixDepth.postTranslate(transX, transY)
                matrixRotate.postConcat(matrixDepth)
            }
            if (hasAtmospheric) {
                var alpha =
                    ((drawnCenterY - Math.abs(drawnCenterY - mDrawnItemCenterY)) * 1.0f / drawnCenterY
                            * 255).toInt()
                alpha = if (alpha < 0) 0 else alpha
                paintTextSelect?.alpha = alpha
                paintTextNormal?.alpha = alpha
            }
            // Correct item's drawn centerY base on curved state
            val drawnCenterY =
                if (isCurved) drawnCenterY - distanceToCenter else mDrawnItemCenterY.toFloat()

            // Judges need to draw different color for current item or not
            paintTextSelect?.let {
                canvas.save()
                if (isCurved) canvas.concat(matrixRotate)
                canvas.clipRect(rectCurrentItem, Region.Op.DIFFERENCE)
                if (paintTextSelect.alpha < 255) {
                    paintTextSelect.typeface = fontNormalTypeface
                } else {
                    paintTextSelect.typeface = fontSelectedTypeface
                }
                canvas.drawText(data, drawnCenterX.toFloat(), drawnCenterY, paintTextSelect)
                canvas.restore()
                if (mSelectedItemTextColor != -1) {
                    paintTextSelect.color = mSelectedItemTextColor
                }
                canvas.save()
                if (isCurved) canvas.concat(matrixRotate)
                canvas.clipRect(rectCurrentItem)
                if (paintTextSelect.alpha < 255) {
                    paintTextSelect.typeface = fontNormalTypeface
                } else {
                    paintTextSelect.typeface = fontSelectedTypeface
                }
                canvas.drawText(data, drawnCenterX.toFloat(), drawnCenterY, paintTextSelect)
                canvas.restore()

                drawnDataPos++
                drawnOffsetPos++

                if (hasCurtain) {
                    paintTextSelect.color = mCurtainColor
                    paintTextSelect.style = Paint.Style.FILL
                    paintTextNormal?.color = mCurtainColor
                    paintTextNormal?.style = Paint.Style.FILL
                    canvas.drawRect(rectCurrentItem, paintTextSelect)
                }
                // Need to draw indicator or not
//                if (hasIndicator) {
//                    paintTextSelect.color = mIndicatorColor
//                    paintTextSelect.style = Paint.Style.FILL
//                    paintTextNormal?.color = mIndicatorColor
//                    paintTextNormal?.style = Paint.Style.FILL
//                    canvas.drawRect(rectIndicatorHead, paintTextSelect)
//                    canvas.drawRect(rectIndicatorFoot, paintTextSelect)
//                }
            }
        }
    }

    private fun isPosInRang(position: Int): Boolean {
        return position >= 0 && position < adapter!!.getItemCount()
    }

    private fun computeYCoordinateAtAngle(degree: Float): Float {
        // Compute y-coordinate for item at degree. mMaxAngle is at mHalfWheelHeight
        return sinDegree(degree) / sinDegree(mMaxAngle.toFloat()) * mHalfWheelHeight
    }

    private fun sinDegree(degree: Float): Float {
        return Math.sin(Math.toRadians(degree.toDouble())).toFloat()
    }

    private fun computeDepth(degree: Float): Float {
        return (mHalfWheelHeight - Math.cos(Math.toRadians(degree.toDouble())) * mHalfWheelHeight).toFloat()
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        if (value < min) return min
        return if (value > max) max else value
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (null != parent) parent.requestDisallowInterceptTouchEvent(true)
                    if (null == tracker) {
                        tracker = VelocityTracker.obtain()
                    } else {
                        tracker!!.clear()
                    }
                    tracker!!.addMovement(event)
                    if (!scroller.isFinished) {
                        scroller.abortAnimation()
                        isForceFinishScroll = true
                    }
                    run {
                        lastPointY = event.y.toInt()
                        downPointY = lastPointY
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(downPointY - event.y) < touchSlop
                        && computeDistanceToEndPoint(scroller.finalY % mItemHeight) > 0
                    ) {
                        isClick = true
                        return true
                    }
                    isClick = false
                    tracker!!.addMovement(event)
                    if (null != onWheelChangeListener) {
                        onWheelChangeListener!!.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING)
                    }

                    // Scroll WheelPicker's content
                    val move = event.y - lastPointY
                    if (Math.abs(move) < 1) return true
                    scrollOffsetY += move.toInt()
                    lastPointY = event.y.toInt()
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    if (null != parent) parent.requestDisallowInterceptTouchEvent(false)
                    if (isClick) return true
                    tracker!!.addMovement(event)
                    tracker!!.computeCurrentVelocity(1000, maximumVelocity.toFloat())

                    // Judges the WheelPicker is scroll or fling base on current velocity
                    isForceFinishScroll = false
                    val velocity = tracker!!.yVelocity.toInt()
                    if (Math.abs(velocity) > minimumVelocity) {
                        scroller.fling(0, scrollOffsetY, 0, velocity, 0, 0, minFlingY, maxFlingY)
                        scroller.finalY =
                            scroller.finalY + computeDistanceToEndPoint(scroller.finalY % mItemHeight)
                    } else {
                        scroller.startScroll(
                            0, scrollOffsetY, 0,
                            computeDistanceToEndPoint(scrollOffsetY % mItemHeight)
                        )
                    }
                    // Correct coordinates
                    if (!isCyclic) {
                        if (scroller.finalY > maxFlingY) {
                            scroller.finalY = maxFlingY
                        } else if (scroller.finalY < minFlingY) scroller.finalY = minFlingY
                    }
                    post(runnable)
                    if (null != tracker) {
                        tracker!!.recycle()
                        tracker = null
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (null != parent) parent.requestDisallowInterceptTouchEvent(false)
                    if (null != tracker) {
                        tracker!!.recycle()
                        tracker = null
                    }
                }
            }
        }
        return true
    }

    private fun computeDistanceToEndPoint(remainder: Int): Int {
        return if (Math.abs(remainder) > mHalfItemHeight) {
            if (scrollOffsetY < 0) {
                -mItemHeight - remainder
            } else {
                mItemHeight - remainder
            }
        } else {
            -remainder
        }
    }

    fun scrollTo(itemPosition: Int) {
        if (itemPosition != currentItemPosition) {
            val differencesLines = currentItemPosition - itemPosition
            val newScrollOffsetY =
                scrollOffsetY + differencesLines * mItemHeight // % adapter.getItemCount();
            val va = ValueAnimator.ofInt(scrollOffsetY, newScrollOffsetY)
            va.duration = 300
            va.addUpdateListener { animation ->
                scrollOffsetY = animation.animatedValue as Int
                invalidate()
            }
            va.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentItemPosition = itemPosition
                    onItemSelected()
                }
            })
            va.start()
        }
    }

    private fun onItemSelected() {
        val position = currentItemPosition
        val item = getAdapter()?.getItem(position)
        if (null != onItemSelectedListener) {
            onItemSelectedListener!!.onItemSelected(this, item, position)
        }
        onItemSelected(position, item)
    }

    protected open fun onItemSelected(position: Int, item: V?) {
        listener?.onSelected(this, position, item)
    }

    protected open fun onItemCurrentScroll(position: Int, item: V?) {
        if (lastScrollPosition != position) {
            listener?.onCurrentScrolled(this, position, item)
            if (lastScrollPosition == adapter!!.getItemCount() - 1 && position == 0) {
                onFinishedLoop()
            }
            lastScrollPosition = position
        }
    }

    protected open fun onFinishedLoop() {}
    protected open fun getFormattedValue(value: Any): String {
        return value.toString()
    }

    var visibleItemCount: Int
        get() = mVisibleItemCount
        set(count) {
            mVisibleItemCount = count
            updateVisibleItemCount()
            requestLayout()
        }

    fun isCyclic(): Boolean {
        return isCyclic
    }

    open fun setCyclic(isCyclic: Boolean) {
        this.isCyclic = isCyclic
        computeFlingLimitY()
        invalidate()
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        onItemSelectedListener = listener
    }

    fun getSelectedItemPosition(): Int {
        return selectedItemPosition
    }

    private fun setSelectedItemPosition(position: Int) {
        var positionValue = position
        positionValue = min(positionValue, adapter!!.getItemCount() - 1)
        positionValue = max(positionValue, 0)
        selectedItemPosition = positionValue
        currentItemPosition = positionValue
        scrollOffsetY = 0
        computeFlingLimitY()
        requestLayout()
        invalidate()
    }

    private val defaultItemPosition: Int?
        get() = adapter?.getData()?.indexOf(defaultValue)
    private val todayItemPosition: Int
        get() {
            val list = adapter?.getData()
            list?.let {
                for (i in list.indices) {
                    if (list[i] is DateWithLabel) {
                        val (label) = list[i] as DateWithLabel
                        if (label == getLocalizedString(R.string.picker_today)) {
                            return i
                        }
                    }
                }
            }
            return 0
        }

    private fun setAdapter(adapter: Adapter<V>?) {
        this.adapter = adapter
        updateItemTextAlign()
        computeTextSize()
        notifyDatasetChanged()
    }

    fun notifyDatasetChanged() {
        if (selectedItemPosition > adapter!!.getItemCount() - 1
            || currentItemPosition > adapter!!.getItemCount() - 1
        ) {
            currentItemPosition = adapter!!.getItemCount() - 1
            selectedItemPosition = currentItemPosition
        } else {
            selectedItemPosition = currentItemPosition
        }
        scrollOffsetY = 0
        computeTextSize()
        computeFlingLimitY()
        requestLayout()
        postInvalidate()
    }

    fun setSameWidth(hasSameWidth: Boolean) {
        this.hasSameWidth = hasSameWidth
        computeTextSize()
        requestLayout()
        invalidate()
    }

    fun hasSameWidth(): Boolean {
        return hasSameWidth
    }

    fun setOnWheelChangeListener(listener: OnWheelChangeListener?) {
        onWheelChangeListener = listener
    }

    var maximumWidthText: String?
        get() = maxWidthText
        set(text) {
            if (null == text) throw NullPointerException("Maximum width text can not be null!")
            maxWidthText = text
            computeTextSize()
            requestLayout()
            postInvalidate()
        }
    var maximumWidthTextPosition: Int
        get() = textMaxWidthPosition
        set(position) {
            if (!isPosInRang(position)) {
                throw ArrayIndexOutOfBoundsException(
                    "Maximum width text Position must in [0, " +
                            adapter!!.getItemCount() + "), but current is " + position
                )
            }
            textMaxWidthPosition = position
            computeTextSize()
            requestLayout()
            postInvalidate()
        }
    var selectedItemTextColor: Int
        get() = mSelectedItemTextColor
        set(color) {
            mSelectedItemTextColor = color
            computeCurrentItemRect()
            postInvalidate()
        }
    var itemTextColor: Int
        get() = mItemTextColor
        set(color) {
            mItemTextColor = color
            postInvalidate()
        }
    var itemTextSize: Int
        get() = mItemTextSize
        set(size) {
            if (mItemTextSize != size) {
                mItemTextSize = size
                paintTextSelect?.textSize = mItemTextSize.toFloat()
                paintTextNormal?.textSize = mItemTextSize.toFloat()
                computeTextSize()
                requestLayout()
                postInvalidate()
            }
        }
    var itemSpace: Int
        get() = mItemSpace
        set(space) {
            mItemSpace = space
            requestLayout()
            postInvalidate()
        }

    fun setCurvedMaxAngle(maxAngle: Int) {
        mMaxAngle = maxAngle
        requestLayout()
        postInvalidate()
    }

    fun setIndicator(hasIndicator: Boolean) {
        this.hasIndicator = hasIndicator
        computeIndicatorRect()
        postInvalidate()
    }

    fun hasIndicator(): Boolean {
        return hasIndicator
    }

    var indicatorSize: Int
        get() = mIndicatorSize
        set(size) {
            mIndicatorSize = size
            computeIndicatorRect()
            postInvalidate()
        }
    var indicatorColor: Int
        get() = mIndicatorColor
        set(color) {
            mIndicatorColor = color
            postInvalidate()
        }

    fun setCurtain(hasCurtain: Boolean) {
        this.hasCurtain = hasCurtain
        computeCurrentItemRect()
        postInvalidate()
    }

    fun hasCurtain(): Boolean {
        return hasCurtain
    }

    var curtainColor: Int
        get() = mCurtainColor
        set(color) {
            mCurtainColor = color
            postInvalidate()
        }

    fun setAtmospheric(hasAtmospheric: Boolean) {
        this.hasAtmospheric = hasAtmospheric
        postInvalidate()
    }

    fun hasAtmospheric(): Boolean {
        return hasAtmospheric
    }

    fun isCurved(): Boolean {
        return isCurved
    }

    fun setCurved(isCurved: Boolean) {
        this.isCurved = isCurved
        requestLayout()
        postInvalidate()
    }

    var itemAlign: Int
        get() = mItemAlign
        set(align) {
            mItemAlign = align
            updateItemTextAlign()
            computeDrawnCenter()
            postInvalidate()
        }

    fun setTypefaceSelected(tf : Typeface){
        fontSelectedTypeface = tf
    }

    fun setTypefaceNormal(tf : Typeface){
        fontNormalTypeface = tf
    }

    open fun findIndexOfDate(date: Date): Int {
        val formatItem = getFormattedValue(date)
        if (this is WheelDayOfMonthPicker) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            calendar.time = date
            return calendar[Calendar.DAY_OF_MONTH] - 1
        }
        if (this is WheelDayPicker) {
            val today = getFormattedValue(Date())
            if (today == formatItem) {
                return todayItemPosition
            }
        }
        if (this is WheelMonthPicker) {
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            calendar.time = date
            return calendar[Calendar.MONTH]
        }
        if (this is WheelYearPicker) {
            val yearPick = this as WheelYearPicker
            val calendar = Calendar.getInstance()
            calendar.timeZone = dateHelper.getTimeZone()
            calendar.time = date
            return calendar[Calendar.YEAR] - yearPick.getMinYear()
        }
        var formatItemInt = Int.MIN_VALUE
        try {
            formatItemInt = formatItem.toInt()
        } catch (e: NumberFormatException) {
        }
        val itemCount = adapter!!.getItemCount()
        var index = 0
        for (i in 0 until itemCount) {
            val `object` = adapter!!.getItemText(i)
            if (formatItemInt != Int.MIN_VALUE) {
                // displayed values are Integers
                val objectInt = `object`.toInt()
                if (objectInt <= formatItemInt) {
                    index = i
                }
            } else if (formatItem == `object`) {
                return i
            }
        }
        return index
    }

    fun getLocalizedString(@StringRes stringRes: Int): String {
        return getStringLocale(context, currentLocale, stringRes)
    }

    val currentLocale: Locale
        get() {
            if (customLocale != null) {
                return customLocale!!
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resources.configuration.locales[0]
            } else {
                resources.configuration.locale
            }
        }

    companion object {
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SCROLLING = 2
        const val ALIGN_CENTER = 0
        const val ALIGN_LEFT = 1
        const val ALIGN_RIGHT = 2
        const val MAX_ANGLE = 90
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WheelPicker)
        mItemTextSize = a.getDimensionPixelSize(
            R.styleable.WheelPicker_wheel_item_text_size,
            resources.getDimensionPixelSize(R.dimen.WheelItemTextSize)
        )
        mVisibleItemCount = a.getInt(R.styleable.WheelPicker_wheel_visible_item_count, 7)
        selectedItemPosition = a.getInt(R.styleable.WheelPicker_wheel_selected_item_position, 0)
        hasSameWidth = a.getBoolean(R.styleable.WheelPicker_wheel_same_width, false)
        textMaxWidthPosition =
            a.getInt(R.styleable.WheelPicker_wheel_maximum_width_text_position, -1)
        maxWidthText = a.getString(R.styleable.WheelPicker_wheel_maximum_width_text)
        mSelectedItemTextColor =
            a.getColor(R.styleable.WheelPicker_wheel_selected_item_text_color, -1)
        mItemTextColor = a.getColor(R.styleable.WheelPicker_wheel_item_text_color, -0x777778)
        mItemSpace = a.getDimensionPixelSize(
            R.styleable.WheelPicker_wheel_item_space,
            resources.getDimensionPixelSize(R.dimen.WheelItemSpace)
        )
        isCyclic = a.getBoolean(R.styleable.WheelPicker_wheel_cyclic, false)
        hasIndicator = a.getBoolean(R.styleable.WheelPicker_wheel_indicator, false)
        mIndicatorColor = a.getColor(R.styleable.WheelPicker_wheel_indicator_color, -0x11cccd)
        mIndicatorSize = a.getDimensionPixelSize(
            R.styleable.WheelPicker_wheel_indicator_size,
            resources.getDimensionPixelSize(R.dimen.WheelIndicatorSize)
        )
        hasCurtain = a.getBoolean(R.styleable.WheelPicker_wheel_curtain, false)
        mCurtainColor = a.getColor(R.styleable.WheelPicker_wheel_curtain_color, -0x77000001)
        hasAtmospheric = a.getBoolean(R.styleable.WheelPicker_wheel_atmospheric, false)
        isCurved = a.getBoolean(R.styleable.WheelPicker_wheel_curved, false)
        mItemAlign = a.getInt(R.styleable.WheelPicker_wheel_item_align, ALIGN_CENTER)
        a.recycle()
        updateVisibleItemCount()
        paintTextSelect =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
        paintTextSelect.textSize = mItemTextSize.toFloat()
        paintTextNormal =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.LINEAR_TEXT_FLAG)
        paintTextNormal.textSize = mItemTextSize.toFloat()
        scroller = Scroller(getContext())
        val conf = ViewConfiguration.get(getContext())
        minimumVelocity = conf.scaledMinimumFlingVelocity
        maximumVelocity = conf.scaledMaximumFlingVelocity
        touchSlop = conf.scaledTouchSlop
        initSecond()
    }

    private fun initSecond() {
        initFirst()
        defaultValue = initDefault()
        adapter!!.setData(generateAdapterValues(showOnlyFutureDate)!!)
        defaultValue?.let {
            currentItemPosition = adapter!!.getItemPosition(it)
            selectedItemPosition = currentItemPosition
        }
    }

    fun setShowOnlyFutureDate(mustBeOnFuture: Boolean) {
        this.showOnlyFutureDate = mustBeOnFuture
    }

    fun setDateHelper(dateHelper: DateHelper) {
        this.dateHelper = dateHelper
    }
}