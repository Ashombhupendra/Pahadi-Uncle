package com.pahadi.uncle.presentation.home.categories

import android.graphics.Color
import com.pahadi.uncle.presentation.utils.ColorGenerator

data class CategoryItem(val id: Int, val categoryName: String, val iconLink: String) {
    val backgroundColor = Color.parseColor(ColorGenerator.color)
}