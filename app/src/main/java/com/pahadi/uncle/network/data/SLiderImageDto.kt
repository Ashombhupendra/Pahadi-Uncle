package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class SLiderImageDto(
    @SerializedName("banner_link")
    val  sliderImaggeUrl : String,
    @SerializedName("id")
    val id : String,
    @SerializedName("created")
    val created : String,
    @SerializedName("banner_image")
    val bannerimage : String
)