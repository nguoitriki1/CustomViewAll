package com.example.progresscountdowntimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.progresscountdowntimer.grapview.GraphValueInput
import com.example.progresscountdowntimer.grapview.SingleGraphView

class GrapActivity : AppCompatActivity() {

    lateinit var graphView : SingleGraphView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grap)
        graphView = findViewById(R.id.my_grap_view)
        graphView.setNumberCount(8)
        graphView.setBottomTitle("ABC")
        val listValueInput = ArrayList<GraphValueInput>()
        listValueInput.add(GraphValueInput(1f, 250f))
        listValueInput.add(GraphValueInput(2f, 152f))
        listValueInput.add(GraphValueInput(3f, 350f))
        listValueInput.add(GraphValueInput(4f, 0f))
        listValueInput.add(GraphValueInput(5f, 90f))
        listValueInput.add(GraphValueInput(6f, 50f))
        listValueInput.add(GraphValueInput(7f, 299f))
        listValueInput.add(GraphValueInput(8f, 250f))
        graphView.setInputValue(listValueInput)
    }

    override fun onResume() {
        super.onResume()
    }
}