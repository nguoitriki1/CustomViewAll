package com.example.progresscountdowntimer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.progresscountdowntimer.pickerdate.weight.WeightPickerView
import com.example.progresscountdowntimer.roundtransfomation.GlideCircleBorderTransform

class ProfileActivity2 : AppCompatActivity() {
    private lateinit var avtImg : ImageView
    private lateinit var nextBtn : TextView
    private lateinit var weightPicker : WeightPickerView
    private var uriAvt: String? = null
    private lateinit var options: RequestOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)
        avtImg = findViewById<ImageView>(R.id.avt_img)
        nextBtn = findViewById(R.id.button_next)
        weightPicker = findViewById(R.id.picker_weight)
        uriAvt = intent.getStringExtra("abc")

        setupWeightPicker()

        options =
            RequestOptions.bitmapTransform(
                GlideCircleBorderTransform(
                    convertDpToPixel(4, this),
                    Color.WHITE
                )
            )
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        if (uriAvt != null){
            loadImageAvt(Uri.parse(uriAvt))
        }else{
            loadImageAvt(R.drawable.profile_default)
        }

        nextBtn.setOnClickListener {
            val fitValue = weightPicker.fitValue
            val fitValue2 = weightPicker.fitValue2
            val unitHeight = weightPicker.unitHeight
            Log.d("abc","fitValue : $fitValue,fitValue2 : $fitValue2,unitHeight : $unitHeight")
            val intent = Intent(this, ProfileActivity3::class.java)
            uriAvt?.let {
                intent.putExtra("abc",uriAvt.toString())
            }
            startActivity(intent)
        }
    }

    private fun setupWeightPicker() {
        weightPicker.addOnWeightChangedListener(object : WeightPickerView.OnWeightChangedListener{
            override fun onWeightChanged(displayed: String?, value: String?, isCm: Boolean) {
            }

        })
    }

    private fun convertDpToPixel(dp: Int, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun loadImageAvt(uri: Uri) {
        Glide.with(this).load(uri)
            .apply(options)
            .error(R.drawable.profile_default)
            .into(avtImg)
    }

    private fun loadImageAvt(resImg : Int) {
        Glide.with(this).load(resImg)
            .apply(options)
            .into(avtImg)
    }

}