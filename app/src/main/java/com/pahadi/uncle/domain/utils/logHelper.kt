package com.pahadi.uncle.domain.utils

import android.util.Log
import com.pahadi.uncle.BuildConfig

fun log(message: String?) {
    if (BuildConfig.DEBUG) {
        Log.d("kalsi", message?: "log called with null message")
    }
}