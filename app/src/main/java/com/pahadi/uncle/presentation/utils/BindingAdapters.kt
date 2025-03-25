package com.pahadi.uncle.presentation.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.pahadi.uncle.R
import com.pahadi.uncle.presentation.home.categories.CategoryItem
import com.pahadi.uncle.presentation.home.categories.CategoryViewGroup
import com.squareup.picasso.Picasso

@BindingAdapter("image_url")
fun loadImageFromUrl(imageView: ImageView, url: String?) {
    val u = url
        ?: "https://rattan-gardenfurniture.co.uk/pub/media/catalog/product/cache/05fc658b49fbb9cf535f08dd0e04d4dc/s/f/sfs094_2239_076_1_.jpg"
    Glide.with(imageView).load(u).into(imageView)
}

@BindingAdapter("profile_image")
fun loadProfileImage(imageView: ImageView, url: String?) {
    if (url.isNullOrBlank()) {
        imageView.setImageResource(R.drawable.ic_person_tie)
    } else {
       // Glide.with(imageView).load(url).into(imageView)
        Picasso.get().load(url).into(imageView)
    }
}

@BindingAdapter("categories")
fun setCategories(categoryViewGroup: CategoryViewGroup, categories: List<CategoryItem>) {
    categoryViewGroup.setCategories(categories)
}

@BindingAdapter("circular_image_url")
fun loadImage(imageView: CircularImageView, url: String) {
    Glide.with(imageView).load(url).into(imageView)
}

@BindingAdapter("show_border")
fun showBorder(imageView: CircularImageView, shown: Boolean) {
    imageView.borderWidth = if (shown) 5f else 0f
}