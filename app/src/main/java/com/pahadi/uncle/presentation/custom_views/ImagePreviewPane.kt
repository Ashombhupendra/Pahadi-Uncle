package com.pahadi.uncle.presentation.custom_views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.core.view.size
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.pahadi.uncle.R
import com.pahadi.uncle.presentation.utils.dpToPixel
import com.pahadi.uncle.presentation.utils.getThumbnailFromVideo

class ImagePreviewPane @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(
    context,
    attrs,
    defStyle
) {
    private var lastSelectedPosition = -1
    var onThumbnailClicked: ((position: Int) -> Unit)? = null

    companion object {
        const val THUMB_NAIL_SIZE = 40
        const val MARGIN = 9
        const val STROKE_WIDTH = 5
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
    }

    fun addImageThumbNail(url: String) {
        val imageView = ShapeableImageView(context).apply {
            strokeWidth = dpToPixel(context, STROKE_WIDTH).toFloat()
            strokeColor = ColorStateList.valueOf(Color.BLACK)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val layoutParams = LayoutParams(
            dpToPixel(context, THUMB_NAIL_SIZE),
            dpToPixel(context, THUMB_NAIL_SIZE)
        ).also {
            it.marginStart = dpToPixel(context, MARGIN)
            it.marginEnd = dpToPixel(context, MARGIN)
        }

        Glide.with(context).load(url).into(imageView)

        imageView.setOnClickListener {
            val index = this.indexOfChild(it)
            if(index == -1) return@setOnClickListener
            onThumbnailClicked?.invoke(index)
        }

        addView(imageView, layoutParams)
    }

    suspend fun addVideoThumbNail(url: String) {
        val thumbNailBitmap = getThumbnailFromVideo(url)
        val thumbNailImageView = ShapeableImageView(context).apply {
            strokeWidth = dpToPixel(context, STROKE_WIDTH).toFloat()
            strokeColor = ColorStateList.valueOf(Color.BLACK)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        thumbNailImageView.setImageBitmap(thumbNailBitmap)

        val playIconImageView = ImageView(context).apply {
            setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
            scaleType = ImageView.ScaleType.CENTER
        }

        val linearLayoutParams = LayoutParams(
            dpToPixel(context, THUMB_NAIL_SIZE),
            dpToPixel(context, THUMB_NAIL_SIZE)
        ).also {
            it.marginStart = dpToPixel(context, MARGIN)
            it.marginEnd = dpToPixel(context, MARGIN)
        }

        val frameLayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        val frameLayout = FrameLayout(context).apply {
            addView(thumbNailImageView, frameLayoutParams)
            addView(playIconImageView, frameLayoutParams)
            setOnClickListener {
                val index = this@ImagePreviewPane.indexOfChild(it)
                if(index == -1) return@setOnClickListener
                onThumbnailClicked?.invoke(index)
            }
        }

        addView(frameLayout, linearLayoutParams)
    }

    fun setSelected(position: Int) {
        deselectLastSelectedView()
        lastSelectedPosition = position
        selectView(position)
    }

    private fun selectView(position: Int) {
        if (this.size == 0) return
        val selectedView = this[position]
        val scaleXAnim = ObjectAnimator.ofFloat(selectedView, "scaleX", 1.5f)
        val scaleYAnim = ObjectAnimator.ofFloat(selectedView, "scaleY", 1.5f)
        AnimatorSet().apply {
            duration = 400
            playTogether(scaleXAnim, scaleYAnim)
            start()
        }
    }

    private fun deselectLastSelectedView() {
        if (lastSelectedPosition == -1) return
        val lastSelectedView = this[lastSelectedPosition]
        val scaleXAnim = ObjectAnimator.ofFloat(lastSelectedView, "scaleX", 1f)
        val scaleYAnim = ObjectAnimator.ofFloat(lastSelectedView, "scaleY", 1f)
        AnimatorSet().apply {
            duration = 400
            playTogether(scaleXAnim, scaleYAnim)
            start()
        }
    }
}
