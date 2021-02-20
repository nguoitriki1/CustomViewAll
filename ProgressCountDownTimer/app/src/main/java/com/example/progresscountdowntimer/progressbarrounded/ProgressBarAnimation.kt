package com.example.progresscountdowntimer.progressbarrounded

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

/**
 * Created by SASANK on 2/19/2017.
 */
class ProgressBarAnimation(
    private val mProgressBar: ProgressBar,
    private val mFrom: Float,
    private val mTo: Float
) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        val value = mFrom + (mTo - mFrom) * interpolatedTime
        mProgressBar.progress = value.toInt()
    }
}