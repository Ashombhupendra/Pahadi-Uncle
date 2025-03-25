package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class MyCartDTO(

    @SerializedName("id")
    val id : String,
    @SerializedName("product_id")
    val product_id : String,
    @SerializedName("quantity")
    var quantity : String,

    @SerializedName("thumb_image")
    val thumb_image : String,
    @SerializedName("product_name")
    val product_name : String,
    @SerializedName("description")
    val description : String,
    @SerializedName("price")
    val price : String,

)
