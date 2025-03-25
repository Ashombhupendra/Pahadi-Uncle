package com.pahadi.uncle.network.data


import com.google.gson.annotations.SerializedName

data class ImageDto(
    @SerializedName("created")
    val created: String,
    @SerializedName("id")
    val imageId: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_image")
    val productImageName: String
)
