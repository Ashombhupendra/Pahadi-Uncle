package com.pahadi.uncle.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pahadi.uncle.PahadiUncleApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


fun Fragment.showSnackBar(text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
}

suspend fun getThumbnailFromVideo(videoPath: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        MediaMetadataRetriever().let {
            it.setDataSource(
                videoPath,
                HashMap()
            )
            it.frameAtTime
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retrieveVideoFrameFromVideo(String videoPath)" + e.message)
    }
}

fun Fragment.dpToPixel(dp: Int): Int {
    val displayMetrics = requireContext().resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun dpToPixel(context: Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}
suspend fun temp_showToast(text: String) = withContext(Dispatchers.Main) {
    Toast.makeText(
        PahadiUncleApplication.instance,
        text,
        Toast.LENGTH_LONG
    ).show() //todo remove this toast
}

