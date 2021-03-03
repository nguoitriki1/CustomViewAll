package com.example.progresscountdowntimer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.progresscountdowntimer.grapview.GraphValueInput
import com.example.progresscountdowntimer.grapview.SingleGraphView

class GrapActivity : AppCompatActivity() {

    lateinit var graphView : SingleGraphView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grap)
        graphView = findViewById(R.id.my_grap_view)
        graphView.setBottomTitle("ABC")
        graphView.setNumberCount(15)
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

        val buttonSelect = findViewById<Button>(R.id.button3)
        val buttonChangeData = findViewById<Button>(R.id.button4)
        val edtColumn = findViewById<EditText>(R.id.editTextTextPersonName6)
        buttonSelect.setOnClickListener {
            graphView.setCurrentSelected(edtColumn.text.toString().toFloat())
        }
        buttonChangeData.setOnClickListener {
            graphView.setNumberCount(30)
            graphView.setMaxValue(600,5)
            val listValueInput = ArrayList<GraphValueInput>()
            listValueInput.add(GraphValueInput(1f, 10f))
            listValueInput.add(GraphValueInput(2f, 30f))
            listValueInput.add(GraphValueInput(3f, 25f))
            listValueInput.add(GraphValueInput(4f, 0f))
            listValueInput.add(GraphValueInput(5f, 90f))
            listValueInput.add(GraphValueInput(6f, 50f))
            listValueInput.add(GraphValueInput(7f, 100f))
            listValueInput.add(GraphValueInput(8f, 110f))
            listValueInput.add(GraphValueInput(9f, 110f))
            listValueInput.add(GraphValueInput(10f, 0f))
            listValueInput.add(GraphValueInput(11f, 25f))
            listValueInput.add(GraphValueInput(12f, 30f))
            graphView.setInputValue(listValueInput)
        }

        val edtMaxValue = findViewById<TextView>(R.id.editTextTextPersonName7)
        val edtRangeBreak = findViewById<TextView>(R.id.editTextTextPersonName8)
        val edtX = findViewById<TextView>(R.id.editTextTextPersonName6)
        val edtY = findViewById<TextView>(R.id.editTextTextPersonName9)
        val btnChangeRange = findViewById<Button>(R.id.button14)
        val btnAdd = findViewById<Button>(R.id.button3)
        val btnClear = findViewById<Button>(R.id.button13)
        btnAdd.setOnClickListener {
           val pivotX = edtX.text.toString().toFloat()
           val pivotY = edtY.text.toString().toFloat()
            val listCache = ArrayList<GraphValueInput>(listValueInput)
            var isAdd = true
            for (i in 0 until listCache.size){
                if (listCache[i].x == pivotX){
                    isAdd = false
                    listValueInput[i] = GraphValueInput(pivotX,pivotY)
                    break
                }
            }

            if (isAdd){
                listValueInput.add(GraphValueInput(pivotX,pivotY))
            }

            listValueInput.sortBy { it.x }


            graphView.setInputValue(listValueInput)
        }
        btnChangeRange.setOnClickListener {
            graphView.setMaxValue(edtMaxValue.text.toString().toInt(),edtRangeBreak.text.toString().toInt())
        }
        btnClear.setOnClickListener {
            graphView.clearView()
        }


    }

    override fun onResume() {
        super.onResume()
    }
}