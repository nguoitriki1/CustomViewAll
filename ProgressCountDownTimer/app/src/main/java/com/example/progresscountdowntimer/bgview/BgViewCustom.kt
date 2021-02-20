package com.example.progresscountdowntimer.bgview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import com.example.progresscountdowntimer.R

class BgViewCustom(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr)  {
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val topVectorWidthViewPort = 152f
    private val topVectorHeightViewPort = 188f
    private val bottomVectorWidthViewPort = 167f
    private val bottomVectorHeightViewPort = 333f
    private var scaleTop = 1f
    private var scaleBottom = 1f
    var pathTop: Path = PathParser.createPathFromPathData("M61.026,152.271C-8.871,205.542 -70.782,187.665 -93,172.068V-16H152C38.507,1.997 148.397,85.683 61.026,152.271Z")
    var pathBottom: Path = PathParser.createPathFromPathData("M72.162,169.954C-10.689,193.668 -5.1,312.238 8.051,368.559V370.535H209.261C254.237,375.278 269.427,342.869 271.399,326.071V12.849C247.728,12.849 208.275,0.004 164.877,0.004C200.384,0.004 175.726,140.312 72.162,169.954Z")

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pathPaint.color = ContextCompat.getColor(context, R.color.colorVectorBg)
        scaleTop = (0.35f*w)/152
        scaleBottom = (0.45f*w)/167
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.scale(scaleTop,scaleTop,0f,0f)
        canvas.drawPath(pathTop, pathPaint)
        canvas.restore()
        canvas.save()
        canvas.translate(width-bottomVectorWidthViewPort, height-bottomVectorHeightViewPort)
        canvas.scale(scaleBottom,scaleBottom,bottomVectorWidthViewPort,bottomVectorHeightViewPort)
        canvas.drawPath(pathBottom, pathPaint)
        canvas.restore()
    }


}
