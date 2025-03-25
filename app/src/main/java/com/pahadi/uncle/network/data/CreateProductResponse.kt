package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class CreateProductResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("status")
    val status: Boolean
)