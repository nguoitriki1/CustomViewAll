package com.example.progresscountdowntimer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.progresscountdowntimer.pickerdate.SingleDateAndTimePicker
import com.example.progresscountdowntimer.roundtransfomation.GlideCircleBorderTransform
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var avtImg : ImageView
    private lateinit var nextBtn : TextView
    private lateinit var nameEdt : EditText
    private lateinit var datePicker : SingleDateAndTimePicker
    private lateinit var options : RequestOptions
    private var avtUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        avtImg = findViewById<ImageView>(R.id.avt_img)
        nextBtn = findViewById(R.id.button_next)
        nameEdt = findViewById(R.id.name_edt)
        datePicker = findViewById<SingleDateAndTimePicker>(R.id.picker_date)
        setupAvt()
        setupPickerBirthday()

        nextBtn.setOnClickListener {
            val selectedDate = datePicker.getSelectedDate()
            Log.d("abc","selectedDate : $selectedDate")
            Log.d("abc","nameEdt : ${nameEdt.text}")
            val intent = Intent(this, ProfileActivity2::class.java)
            avtUri?.let {
                Log.d("abc","avtUri : ${avtUri.toString()}")
                intent.putExtra("abc",avtUri.toString())
            }
            startActivity(intent)
        }
    }

    private fun setupPickerBirthday() {
        datePicker.addOnDateChangedListener(object : SingleDateAndTimePicker.OnDateChangedListener{
            override fun onDateChanged(displayed: String?, date: Date?) {
                Log.d("abc","displayed : $displayed,date : $date")
            }
        })
    }

    private fun setupAvt() {
        avtImg.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, 1)
        }

        options =
            RequestOptions.bitmapTransform(
                GlideCircleBorderTransform(
                    convertDpToPixel(4, this),
                    Color.WHITE
                )
            )
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        loadImageAvt(R.drawable.profile_default)
    }

    private fun loadImageAvt(resImg : Int) {
        Glide.with(this).load(resImg)
            .apply(options)
            .into(avtImg)
    }

    private fun loadImageAvt(uri: Uri) {
        Glide.with(this).load(uri)
            .apply(options)
            .error(R.drawable.profile_default)
            .into(avtImg)
    }

    fun convertDpToPixel(dp: Int, context: Context): Float {
        return dp * (context.getResources().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = data?.data
                selectedImage?.let {
                    avtUri = selectedImage
                    loadImageAvt(it)
                }
            }
        }
    }
}