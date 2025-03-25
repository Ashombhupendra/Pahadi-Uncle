package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.domain.utils.BASE_URL

data class ProductDto(
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("featured")
    val featured: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("modified")
    val modified: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("product_condition")
    val product_condition: String,
    @SerializedName("district")
    val product_district: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("profile_id")
    val profileId: String,
    @SerializedName("specification")
    val specification: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("product_available_status")
    val activeStatus: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("location")
    val location: String?,
    @SerializedName("unit")
    val unit:String,

    @SerializedName("wishlist")
    var wishlist : Int,
    @SerializedName("username")
    val username:String?,
    @SerializedName("profile_img")
    val profileImage:String,

    @SerializedName("ratings")
    val ratings : Float,
    @SerializedName("total_ratings")
    val total_ratings : Int,

    @SerializedName("thumb_image")
    val thumb_image : String
)
