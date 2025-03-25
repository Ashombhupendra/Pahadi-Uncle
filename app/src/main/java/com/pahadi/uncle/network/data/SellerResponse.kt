package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class SellerResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("body")
    val sellerDto: SellerDto
)
