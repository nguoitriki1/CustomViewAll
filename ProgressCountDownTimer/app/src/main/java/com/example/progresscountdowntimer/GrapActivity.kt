package com.example.progresscountdowntimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.broooapps.graphview.CurveGraphConfig
import com.example.progresscountdowntimer.grapview.CurveGraphView
import com.example.progresscountdowntimer.grapview.models.GraphData
import com.example.progresscountdowntimer.grapview.models.PointMap

class GrapActivity : AppCompatActivity() {

    var curveGraphView: CurveGraphView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grap)
        curveGraphView = findViewById(R.id.cgv)
        curveGraphView?.let {
            it.configure(
                CurveGraphConfig.Builder(this)
                    .setAxisColor(R.color.colorAccent) // Set number of values to be displayed in X ax
                    .setVerticalGuideline(4) // Set number of background guidelines to be shown.
                    .setHorizontalGuideline(2)
                    .setGuidelineColor(R.color.blackColor) // Set color of the visible guidelines.
                    .setNoDataMsg(" No Data ") // Message when no data is provided to the view.
                    .setxAxisScaleTextColor(R.color.blackColor) // Set X axis scale text color.
                    .setyAxisScaleTextColor(R.color.blackColor) // Set Y axis scale text color
                    .setAnimationDuration(2000) // Set Animation Duration
                    .build()
            )
            val p2 = PointMap()
            p2.addPoint(0, 440)
            p2.addPoint(1, 0)
            p2.addPoint(2, 0)
            p2.addPoint(3, 0)
            p2.addPoint(4, 0)
            p2.addPoint(5, 0)
            p2.addPoint(6, 0)
            p2.addPoint(7, 0)
            p2.addPoint(8, 100)
            p2.addPoint(9, 0)
            p2.addPoint(10, 400)
            p2.addPoint(11, 200)
            val gd2: GraphData = GraphData.builder(this)
                .setPointMap(p2)
                .setGraphStroke(R.color.colorAccent)
                .setGraphGradient(R.color.gradientStartColor, R.color.gradientEndColor)
                .animateLine(true)
                .setPointColor(R.color.colorYellow)
                .setPointRadius(5)
                .build()

              it.post {
                  it.setData(11, 800, gd2)
              }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}