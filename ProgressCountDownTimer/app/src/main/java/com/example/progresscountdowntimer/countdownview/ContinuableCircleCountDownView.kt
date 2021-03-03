/**
 * Copyright Serhat Sürgüveç All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.progresscountdowntimer.countdownview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.example.progresscountdowntimer.R
import kotlin.math.min

class ContinuableCircleCountDownView : View {
    private var oldtimeMilis: Long = DEFAULT_TIME.toLong()
    private var rect: RectF? = null
    private var textPoint: PointF? = null
    private var paint: Paint = Paint()
    private var textPaint: Paint = Paint()
    private var size = DEFAULT_SIZE
    private var rate = DEFAULT_RATE
    private var textSize = 0f
    private var timeMilis: Long = DEFAULT_TIME.toLong()
    private var intervalTimeMilis = DEFAULT_INTERVAL.toLong()
    private var progressColor = DEFAULT_PROGRESS_COLOR
    private var textColor = DEFAULT_TEXT_COLOR
    private var listener: OnCountDownCompletedListener? = null
    private var timer: CountDownTimer? = null
    private var angleAnimation: CircleAngleAnimation? = null
    private var isFinished = false
    private var isStopped = false
    private var isStarted = false
    private var mMillisUntilFinished: Long = 0
    private var angle = 0f
    private var previousLengthOfText = -1
    private var paintShadow: Paint = Paint()
    private val shadowColor = Color.LTGRAY

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setAttributes(context, attrs)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.ContinuableCircleCountDownView, 0, 0)
        val progressStateList =
            a.getColorStateList(R.styleable.ContinuableCircleCountDownView_progressColor)
        val time = a.getInt(R.styleable.ContinuableCircleCountDownView_defaultTime, DEFAULT_TIME)
        if (progressStateList != null) {
            progressColor = progressStateList.defaultColor
        }
        val textStateList =
            a.getColorStateList(R.styleable.ContinuableCircleCountDownView_textColor)
        if (textStateList != null) {
            textColor = textStateList.defaultColor
        }
        textSize = a.getDimension(
            R.styleable.ContinuableCircleCountDownView_textSize,
            DEFAULT_TEXT_SIZE.toFloat()
        )
        a.recycle()
        init()
        setTimer(time.toLong())
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun init() {
        paint.let {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.color = progressColor
            it.strokeJoin = Paint.Join.ROUND
            it.strokeCap = Paint.Cap.ROUND
        }
        paintShadow.let {
            it.isAntiAlias = true
            it.style = Paint.Style.STROKE
            it.color = shadowColor
        }
        textPaint.let {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.color = textColor
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //make sure square
        size = min(w, h)
        initMeasurements()
    }

    private fun initMeasurements() {
        size -= DEFAULT_PADDING
        val strokeWidth = size / rate
        rect = RectF(
            strokeWidth / 2f + strokeWidth + DEFAULT_PADDING,
            strokeWidth / 2f + strokeWidth + DEFAULT_PADDING,
            size - (strokeWidth / 2f + strokeWidth),
            size - (strokeWidth / 2f + strokeWidth)
        )
        paint.strokeWidth = strokeWidth.toFloat()
        paintShadow.strokeWidth = strokeWidth.toFloat()
        textPaint.textSize = textSize
        calculateTextPosition()
    }

    fun getProgressColor(): Int {
        return progressColor
    }

    fun setProgressColor(PROGRESS_COLOR: Int) {
        this.progressColor = PROGRESS_COLOR
        paint.color = PROGRESS_COLOR
        invalidate()
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun setTextColor(TEXT_COLOR: Int) {
        this.textColor = TEXT_COLOR
        textPaint.color = TEXT_COLOR
        invalidate()
    }


    fun setTimer(seconds: Long) {
        var secondsValue = seconds
        cancel()
        secondsValue *= 1000
        require(secondsValue <= MAX_TIME) { "millis must be lower than 60000" }
        require(secondsValue >= 1) { "millis must be greater than 0" }
        oldtimeMilis = secondsValue
        timeMilis = secondsValue
        mMillisUntilFinished = timeMilis
        this.invalidate()
    }

    fun setTimer(seconds: Long, interval: Long) {
        var secondsValue = seconds
        cancel()
        secondsValue *= 1000
        require(secondsValue <= MAX_TIME) { "millis must be lower than 60000" }
        require(secondsValue >= 1) { "millis must be greater than 0" }
        require(interval >= 0) { "interval must be greater than 0" }
        require(interval < secondsValue) { "interval must be lower than millis" }
        oldtimeMilis = secondsValue
        timeMilis = secondsValue
        intervalTimeMilis = interval
        mMillisUntilFinished = timeMilis
        this.invalidate()
    }

    private fun calculateTextPosition() {
        rect?.let {
            //Text Size Calculations
            val bounds = RectF(it)
            // measure text width
            bounds.right = textPaint.measureText(
                (mMillisUntilFinished / 100).toString(),
                0,
                (mMillisUntilFinished / 1000).toString().length
            )
            // measure text height
            bounds.bottom = textPaint.descent() - textPaint.ascent()
            bounds.left += (it.width() - bounds.right) / 2.0f
            bounds.top += (it.height() - bounds.bottom) / 2.0f

            //text
            textPoint = PointF(bounds.left, bounds.top - textPaint.ascent())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect?.let {
            canvas.drawArc(it, 0f, 360f, false, paintShadow)
            canvas.drawArc(it, START_ANGLE_POINT.toFloat(), angle, false, paint)
            val secs: String = (mMillisUntilFinished / 1000).toString()
            if (secs.length != previousLengthOfText) {
                calculateTextPosition()
                previousLengthOfText = secs.length
            }
            textPoint?.let { pointF ->
                canvas.drawText(secs, pointF.x, pointF.y, textPaint)
            }
        }
    }

    fun addTimeSeconds(timeSeconds: Int) {
        var secondsValue = timeSeconds
        secondsValue *= 1000
        val timeFinished = timeMilis - mMillisUntilFinished +1000
        timeMilis += secondsValue
        mMillisUntilFinished += secondsValue
        if (mMillisUntilFinished <= 0) {
            timeMilis = 0
            mMillisUntilFinished = 0
        } else if (mMillisUntilFinished >= MAX_TIME) {
            timeMilis = MAX_TIME
            mMillisUntilFinished = MAX_TIME
        } else {
            oldtimeMilis = timeMilis
        }

        if (!isStarted && !isStopped) {
            if (mMillisUntilFinished == 0L && timeMilis == 0L) {
               cancel()
            } else {
                invalidate()
            }
        }
        if (isStopped) {
            if (mMillisUntilFinished == 0L && timeMilis == 0L) {
                timeMilis = oldtimeMilis
                cancel()
            } else {
                val calculateAngle = calculateAngle(timeFinished /1000f)
                if (timeSeconds < 0){
                    if (calculateAngle > angle){
                        angle = calculateAngle
                    }
                }else if (timeSeconds > 0){
                    if (calculateAngle < angle){
                        angle = calculateAngle
                    }
                }
                invalidate()
            }
        }
        if (isStarted) {
            if (mMillisUntilFinished == 0L && timeMilis == 0L) {
                cancel()
                return
            } else {
                stop()
                val calculateAngle = calculateAngle(timeFinished / 1000f)
                if (timeSeconds < 0){
                    if (calculateAngle > angle){
                        angle = calculateAngle
                    }
                }else if (timeSeconds > 0){
                    if (calculateAngle < angle){
                        angle = calculateAngle
                    }
                }
                continueE()
            }
        }
    }

    fun start() {

        //Already Running or Finished..
        if (isStarted || isFinished) return

        //set bools
        isFinished = false
        isStopped = false
        isStarted = true

        //Start timer
        timer = object : CountDownTimer(timeMilis, intervalTimeMilis) {
            override fun onTick(millisUntilFinished: Long) {
                mMillisUntilFinished = millisUntilFinished
                invalidate()
                Log.d("abc","time  : ${(timeMilis - mMillisUntilFinished)/1000f}")
                listener?.onTick(timeMilis - mMillisUntilFinished)
            }

            override fun onFinish() {
                mMillisUntilFinished = 0
                listener?.onCompleted()
                isStarted = false
                isFinished = true
                isStopped = false
                finish()
            }
        }.start()

        //Start angle animation
        angleAnimation = CircleAngleAnimation(this, 360)
        angleAnimation?.setUseOffset(false)
        angleAnimation?.duration = timeMilis
        angleAnimation?.fillAfter = true
        startAnimation(angleAnimation)
    }

    /**
     * Continues
     */
    fun continueE() {

        //Used when stopped.
        if (isStopped) {

            //set bools
            isStarted = true
            isFinished = false
            isStopped = false

            //Start timer
            timer = object : CountDownTimer(mMillisUntilFinished, intervalTimeMilis) {
                override fun onTick(millisUntilFinished: Long) {
                    mMillisUntilFinished = millisUntilFinished
                    invalidate()
                    listener?.onTick(timeMilis - mMillisUntilFinished)
                }

                override fun onFinish() {
                    mMillisUntilFinished = 0
                    listener?.onCompleted()
                    isStarted = false
                    isFinished = true
                    isStopped = false
                    finish()
                }
            }.start()

            //Reuse animation
            angleAnimation?.let {
                it.reset()
                it.setUseOffset(false)
                it.duration = mMillisUntilFinished
                it.fillAfter = true
                startAnimation(it)
            }
        }
    }

    fun cancel() {
        isStarted = false
        isFinished = false
        isStopped = false
        timer?.cancel()
        angleAnimation?.cancel()
        clearAnimation()
        angleAnimation = null
        angle = 0f
        if (timeMilis <= 0)
            timeMilis = oldtimeMilis
        mMillisUntilFinished = timeMilis
        this.invalidate()
    }

    fun finish() {
        timer?.cancel()
        angleAnimation?.cancel()
        clearAnimation()
        angleAnimation = null
        angle = 0f
        mMillisUntilFinished = timeMilis
        this.invalidate()
    }

    fun stop() {
        if (isStarted) {
            isStarted = false
            isFinished = false
            isStopped = true
            timer?.cancel()
            clearAnimation()
        }
    }

    fun setListener(listener: OnCountDownCompletedListener?) {
        this.listener = listener
    }

    fun startFrom(seconds: Long, animate: Boolean) {


        var angleValue = calculateAngle(seconds.toFloat())
        angleValue %= 360

        if (isStarted) {
            return
        } else {
            isFinished = false
            isStopped = false
            isStarted = true

            //Calculating
            val offset = (timeMilis * (angleValue / 360)).toLong()
            val timeShouldBegin = timeMilis - offset

            //Start timer
            timer = object : CountDownTimer(timeShouldBegin, intervalTimeMilis) {
                override fun onTick(millisUntilFinished: Long) {
                    mMillisUntilFinished = millisUntilFinished
                    invalidate()
                    listener?.onTick(timeMilis - mMillisUntilFinished)
                }

                override fun onFinish() {
                    mMillisUntilFinished = 0
                    listener?.onCompleted()
                    isStarted = false
                    isFinished = true
                    isStopped = false
                    finish()
                }
            }.start()
            if (animate) {
                animateToOffsetLocation(angleValue, object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        //Start angle animation
                        angleAnimation =
                            CircleAngleAnimation(this@ContinuableCircleCountDownView, 360)
                        angleAnimation?.setUseOffset(false)
                        angleAnimation?.duration = timeShouldBegin
                        angleAnimation?.fillAfter = true
                        startAnimation(angleAnimation)
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            } else {

                //Start angle animation
                angleAnimation = CircleAngleAnimation(this@ContinuableCircleCountDownView, 360)
                angleAnimation?.setOffSet(angleValue)
                angleAnimation?.setUseOffset(true)
                angleAnimation?.duration = timeShouldBegin
                angleAnimation?.fillAfter = true
                startAnimation(angleAnimation)
            }
        }
    }

    private fun calculateAngle(seconds: Float): Float {
        val maxTimeSeconds = timeMilis / 1000f

        return if (seconds >= maxTimeSeconds) {
            360f
        } else {
            val fl = maxTimeSeconds / 360f
            seconds / fl
        }
    }

    private fun animateToOffsetLocation(offset: Float, listener: Animation.AnimationListener) {
        val animation = CircleAngleAnimation(this, offset.toInt())
        animation.duration = 500
        animation.fillAfter = true
        animation.setAnimationListener(listener)
        startAnimation(animation)
    }

    class CircleAngleAnimation(circle: ContinuableCircleCountDownView, newAngle: Int) :
        Animation() {
        private val circle: ContinuableCircleCountDownView
        private var oldAngle: Float
        private val newAngle: Float
        private var offSet: Float
        private var useOffset = false
        override fun reset() {
            super.reset()
            oldAngle = circle.angle
        }

        fun setOffSet(offSet: Float) {
            this.offSet = offSet
        }

        fun setUseOffset(useOffset: Boolean) {
            this.useOffset = useOffset
        }

        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            var off = 0.0f
            if (useOffset) off = offSet

            //Calculates Angle
            val angle = off + oldAngle + (newAngle - oldAngle - off) * interpolatedTime

            //Sets Angle
            circle.angle = angle

            //Invalidate for Layer Type Software
            circle.invalidate()
        }

        init {
            oldAngle = circle.angle
            this.newAngle = newAngle.toFloat()
            this.circle = circle
            offSet = 0.0f
            this.interpolator = LinearInterpolator()
        }
    }

    interface OnCountDownCompletedListener {
        fun onTick(passedMillis: Long)
        fun onCompleted()
    }

    companion object {
        private const val START_ANGLE_POINT = 270
        private const val DEFAULT_SIZE = 200
        private const val DEFAULT_RATE = 25
        private const val DEFAULT_TIME = 10000
        private const val DEFAULT_INTERVAL = 1000
        private val DEFAULT_PROGRESS_COLOR = Color.parseColor("#FF0000")
        private val DEFAULT_TEXT_COLOR = Color.parseColor("#000000")
        private const val DEFAULT_PADDING = 20
        private const val DEFAULT_TEXT_SIZE = 12 //sp
        private const val MAX_TIME = Long.MAX_VALUE
    }
}