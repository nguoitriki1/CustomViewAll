package com.example.progresscountdowntimer

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList


class TextOnSpeedPhoto : AppCompatActivity() {
    private var write: TextView? = null
    private var t1: TextToSpeech? = null
    private val listStringInput: MutableList<String> = ArrayList()
    private var index = 0
    private var secord = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_on_speed_photo)
        write = findViewById<View>(R.id.textView19) as TextView
        initListStringValue()
        speakTextInit()
        startCoundown((listStringInput.size* 2000).toLong())

    }

    private fun startCoundown(time : Long,interval : Long = 2000) {
        val timer = object: CountDownTimer(time, interval) {
            override fun onTick(millisUntilFinished: Long) {
                if (secord != millisUntilFinished/1000){
                    if (index in 0 until listStringInput.size){
                        write?.text =  listStringInput[index]
                        beginSpeak( listStringInput[index], index.toString())
                        index++
                    }
                    secord = millisUntilFinished/1000
                }
            }

            override fun onFinish() {
                index = 0
                beginSpeak("End Speak", listStringInput.size.toString())
            }
        }
        index = 0
        timer.start()
    }

    private fun beginSpeak(textSpeak: String, id: String) {
        t1?.let {
            if (it.isSpeaking) {
                it.stop()
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    it.speak(textSpeak, TextToSpeech.QUEUE_FLUSH, null, id)
                } else {
                    t1?.speak(textSpeak, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
    }

    private fun speakTextInit() {
        t1 = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                t1?.language = Locale.ENGLISH
            }
        }

        t1?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
            }

            override fun onError(utteranceId: String?) {
            }

        })

    }

    private fun initListStringValue() {
        listStringInput.add("Ready")
        listStringInput.add("3")
        listStringInput.add("2")
        listStringInput.add("1")
        listStringInput.add("Ok, Start Now")
        listStringInput.add("1")
        listStringInput.add("2")
        listStringInput.add("3")
        listStringInput.add("4")
        listStringInput.add("5")
        listStringInput.add("6")
        listStringInput.add("7")
        listStringInput.add("8")
        listStringInput.add("9")
        listStringInput.add("10")
    }

    override fun onDestroy() {
        //Dont forget to shut down text to speech
        if (t1 != null) {
            t1!!.stop()
            t1!!.shutdown()
        }
        super.onDestroy()
    }
}