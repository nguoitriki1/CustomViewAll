package com.example.progresscountdowntimer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.progresscountdowntimer.countdownview.ContinuableCircleCountDownView
import com.example.progresscountdowntimer.progressbarrounded.RoundedHorizontalProgressBar

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private var countDownView: ContinuableCircleCountDownView? = null

    private var btnCancel: Button? = null
    private var btnStart: Button? = null
    private var btnStop: Button? = null
    private var btnContinue: Button? = null
    private var add5Seconds: Button? = null
    private var remove5Seconds: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        countDownView = findViewById<ContinuableCircleCountDownView>(R.id.circleCountDownView)
        btnCancel = findViewById<Button>(R.id.btnCancel)
        btnStart = findViewById<Button>(R.id.btnStart)
        btnStop = findViewById<Button>(R.id.btnStop)
        btnContinue = findViewById<Button>(R.id.btnContinue)
        countDownView?.setListener(object : ContinuableCircleCountDownView.OnCountDownCompletedListener {
            override fun onTick(passedMillis: Long) {
                Log.w(TAG, "Tick.$passedMillis")
            }

            override fun onCompleted() {
                Log.w(TAG, "Completed.")
                btnCancel?.isEnabled = true
                btnContinue?.isEnabled = false
                btnStop?.isEnabled = false
                btnStart?.isEnabled = false
            }
        })
        init()
        add5Seconds = findViewById<Button>(R.id.btn_add_5)
        remove5Seconds = findViewById<Button>(R.id.btn_remove_5)
        val progress = findViewById<RoundedHorizontalProgressBar>(R.id.progress_bar_4)
        progress.setTimeCountDown(12)
        progress.setOnClickListener {
            progress.animateProgress(40)
            progress.setSelectedProgress(false)
        }
        add5Seconds?.setOnClickListener {
            countDownView?.addTimeSeconds(5)
        }
        remove5Seconds?.setOnClickListener {
            countDownView?.addTimeSeconds(-5)
        }
    }

    private fun init() {
        btnCancel?.isEnabled = false
        btnContinue?.isEnabled = false
        btnStop?.isEnabled = false
        btnStart?.isEnabled = true
    }

    fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btnStart -> {
                btnCancel?.isEnabled = true
                btnContinue?.isEnabled = false
                btnStop?.isEnabled = true
                btnStart?.isEnabled = false
                countDownView?.start()

            }
            R.id.btnStop -> {
                btnCancel?.isEnabled = true
                btnContinue?.isEnabled = true
                btnStop?.isEnabled = false
                btnStart?.isEnabled = false
                countDownView?.stop()
            }
            R.id.btnContinue -> {
                btnCancel?.isEnabled = true
                btnContinue?.isEnabled = false
                btnStop?.isEnabled = true
                btnStart?.isEnabled = false
                countDownView?.continueE()
            }
            R.id.btnCancel -> {
                btnCancel?.isEnabled = false
                btnContinue?.isEnabled = false
                btnStop?.isEnabled = false
                btnStart?.isEnabled = true
                countDownView?.cancel()
            }
            else -> {
                btnCancel?.isEnabled = false
                btnContinue?.isEnabled = false
                btnStop?.isEnabled = false
                btnStart?.isEnabled = true
                countDownView?.cancel()
            }
        }
    }
}