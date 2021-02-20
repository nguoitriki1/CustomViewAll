package com.example.progresscountdowntimer.progressbarrounded

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.DEFAULT_BOLD
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import com.example.progresscountdowntimer.R

/**
 * RoundedHorizontalProgressBar - An Android custom rounded Progress Bar that supports different Colors as progress
 */
class RoundedHorizontalProgressBar(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
    ProgressBar(context, attrs, defStyleAttr)  {
    private var animCount: ValueAnimator? = null
    private val maxAnimation: Long = 1000
    private val minAnimation: Long = 500
    private lateinit var stringValue: String
    private val textPaint: Paint = Paint()
    private val mRectView = Rect()
    private var textPoint: PointF? = null
    private var isSelectedProgress: Boolean = false

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        textPaint.let {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.color = Color.RED
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectView.set(0, 0, w, h)
        textPaint.textSize = convertPixelsToDp(h.toFloat(), context)
        textPaint.color = Color.WHITE
        textPaint.typeface = DEFAULT_BOLD
        calculateTextPosition()

    }

    fun setTimeCountDown(seconds : Int){
        animCount?.cancel()
        if (seconds <= 0 || seconds >= Int.MAX_VALUE){
            return
        }
        animateProgress(0)
        val stepProgress = 100/seconds
        var valueStep = 0
        animCount = ValueAnimator().apply {
            setIntValues(0, seconds)
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                stringValue = "${animatedValue}/$seconds"
                calculateTextPosition()
                invalidate()
                if (animatedValue != valueStep){
                    valueStep = animatedValue
                    if (valueStep == 0){
                        animateProgress(0)
                    }else{
                        animateProgress(progress+stepProgress)
                    }
                }
            }
            interpolator = LinearInterpolator()
            duration = (seconds*1000).toLong()
            repeatCount = -1
            repeatMode = ValueAnimator.RESTART

        }
        animCount?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animCount?.cancel()
    }

    fun setSelectedProgress(select : Boolean){
        this.isSelectedProgress = select
        progressDrawable = if (select){
            AppCompatResources.getDrawable(context, R.drawable.rounded_progress_bar_horizontal);
        }else{
            AppCompatResources.getDrawable(context, R.drawable.progress_bar_horizontal);
        }
    }

    private fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun calculateTextPosition() {
        mRectView.let {
            //Text Size Calculations
            val bounds = RectF(it)
            // measure text width
            bounds.right = textPaint.measureText(
                stringValue, 0, stringValue.length
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
        textPoint?.let { pointF ->
            canvas.drawText(stringValue, pointF.x, pointF.y, textPaint)
        }
    }

    fun animateProgress(to: Int) {
        val valueTo = when {
            to > 100 -> {
                100
            }
            to < 0 -> {
                0
            }
            else -> {
                to
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            setProgress(valueTo,true)
        }else{
            val valueDuration = valueTo - progress
            val progressBarAnimation = ProgressBarAnimation(
                this,
                progress.toFloat(), valueTo.toFloat()
            )
            if (valueDuration <= 30){
                progressBarAnimation.duration = maxAnimation
            }else{
                progressBarAnimation.duration = minAnimation
            }
            startAnimation(progressBarAnimation)
        }
    }

}