package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName


data class OrderHistoryDTO(

    @SerializedName("id")
    val user_id : String,

    @SerializedName("product_id")
    val product_id : String,

    @SerializedName("product_name")
    val product_name : String,

    @SerializedName("product_qty")
    val product_quantity : String,

    @SerializedName("tracking_id")
    val trackingID : String,

    @SerializedName("product_price")
    val product_amount : String,

    @SerializedName("order_status")
    val order_status : String,

    @SerializedName("order_date")
    val order_date : String
)
