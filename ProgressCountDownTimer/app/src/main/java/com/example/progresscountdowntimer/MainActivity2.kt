package com.example.progresscountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progresscountdowntimer.progressbarrounded.RoundedHorizontalProgressBar

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val roundedHorizontalProgressBar = findViewById<RoundedHorizontalProgressBar>(R.id.progress_bar_4)
        roundedHorizontalProgressBar.setTimeCountDown(12)
    }
}