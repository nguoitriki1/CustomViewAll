package com.example.progresscountdowntimer.pickerdate.datepicker

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.StringRes
import java.util.*

object SingleDateAndTimeConstants {
    const val DAYS_PADDING = 364
    const val MIN_YEAR_DIFF = 50
    const val MAX_YEAR_DIFF = 50
}

fun getStringLocale(context: Context, locale: Locale, @StringRes resourceId: Int): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config).getString(resourceId)
    } else {
        val resources = context.resources
        val conf = resources.configuration
        val savedLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conf.locales
        } else {
            conf.locale
        }
        resources.updateConfiguration(conf, null)

        val result = resources.getString(resourceId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(savedLocale as Locale?)
        }else{
            conf.locale = savedLocale as Locale?
        }
        resources.updateConfiguration(conf, null)
        result
    }
}