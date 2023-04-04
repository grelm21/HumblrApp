package com.example.humblr.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

fun Long.publishedTime(): String {

    val sdf = SimpleDateFormat("KK:mm a dd LLL yy")

    return sdf.format(this * 1000)
}