package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class RegisterDto(
    @SerializedName("status")
    val status:String,
    @SerializedName("message")
    val message:String,
    @SerializedName("user_detail")
    val userDto: UserDto
)
