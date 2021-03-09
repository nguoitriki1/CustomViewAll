package com.example.progresscountdowntimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val buttonCountdownProgress = findViewById<Button>(R.id.button5)
        val buttonSettingActivity = findViewById<Button>(R.id.button6)
        val buttonProfileActivity = findViewById<Button>(R.id.button7)
        val buttonEndExScreen = findViewById<Button>(R.id.button8)
        val buttonCalendar = findViewById<Button>(R.id.button9)
        val buttonDatePicker = findViewById<Button>(R.id.button10)
        val buttonTextToSpeech = findViewById<Button>(R.id.button11)
        buttonCountdownProgress.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        buttonSettingActivity.setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
        }
        buttonProfileActivity.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        buttonEndExScreen.setOnClickListener {
            startActivity(Intent(this,EndExrciseActivity::class.java))
        }
        buttonCalendar.setOnClickListener {
            startActivity(Intent(this,CalendarActivity::class.java))
        }
        buttonDatePicker.setOnClickListener {
            startActivity(Intent(this,GrapActivity::class.java))
        }
        buttonTextToSpeech.setOnClickListener {
            startActivity(Intent(this,GrapActivity::class.java))
        }
    }
}