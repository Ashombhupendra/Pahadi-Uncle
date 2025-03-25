package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class NotificationDTO(
    @SerializedName("id")
    val id : String,

    @SerializedName("user_id")
    val user_id : String,

    @SerializedName("sender_id")
    val sender_id : String,

    @SerializedName("message")
    val message : String,

    @SerializedName("created")
    val created : String,

    @SerializedName("notification_type")
    val notification_type : String,

    @SerializedName("email")
    val email : String,
    @SerializedName("phone")
    val Phone : String ,
    @SerializedName("username")
    val username : String,

    @SerializedName("product_id")
    val productid : String,

    @SerializedName("profile_pic")
    val profile_pic : String
)
