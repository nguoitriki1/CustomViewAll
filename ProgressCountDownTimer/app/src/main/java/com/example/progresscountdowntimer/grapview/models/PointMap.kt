package com.example.progresscountdowntimer.grapview.models

import java.util.*

class PointMap {
    var maxValue = 0
    val pointMap: HashMap<Int, Int> = HashMap()
    fun addPoint(spanPos: Int, value: Int) {
        if (maxValue < value) {
            maxValue = value
        }
        pointMap[spanPos] = value
    }

    operator fun get(spanPos: Int): GraphPoint {
        return if (pointMap.containsKey(spanPos)) {
            GraphPoint(spanPos, pointMap[spanPos]!!)
        } else {
            GraphPoint(spanPos, 0)
        }
    }

}