package com.example.progresscountdowntimer.roundtransfomation

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class GlideCircleBorderTransform(private val borderWidth: Float, private val borderColor: Int) :
    BitmapTransformation() {
    private val ID = javaClass.name
    private val mBorderPaint: Paint = Paint()
    private fun circleCrop(bitmapPool: BitmapPool, source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val square = Bitmap.createBitmap(source, x, y, size, size)
        val result = bitmapPool[size, size, Bitmap.Config.ARGB_8888]

        //Drawing
        val canvas = Canvas(result)
        val paint = Paint()
        //Setting up the Shader
        paint.shader = BitmapShader(square, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val radius = size / 2f
        //Draw a circle
        canvas.drawCircle(radius, radius, radius, paint)


        //Note: avoid trimming by screen edge
        val borderRadius = radius - borderWidth / 2
        //Draw borders
        canvas.drawCircle(radius, radius, borderRadius, mBorderPaint)
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun equals(o: Any?): Boolean {
        return o is GlideCircleBorderTransform
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return circleCrop(pool, toTransform)
    }

    init {
        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.strokeWidth = borderWidth
        mBorderPaint.isDither = true
    }
}