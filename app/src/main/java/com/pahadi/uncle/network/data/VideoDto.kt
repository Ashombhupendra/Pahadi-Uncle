package com.pahadi.uncle.network.data


import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("created")
    val created: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_video")
    val productVideoName: String
)