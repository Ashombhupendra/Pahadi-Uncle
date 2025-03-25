package com.pahadi.uncle.presentation.home

data class Product(
    val imageUrl: String,
    val priceFormatted: String,
    val title: String,
    val featured: Boolean,
    val description: String,
    val outOfStock: Boolean,
    val wishlist : Boolean,
     val showwishlist : Boolean,
    val ratings : Float,
    val total_rating : Int

)
