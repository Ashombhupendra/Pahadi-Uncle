package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val loginPermitted: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("otp")
    val otp: String,
    @SerializedName("user_detail")
    val userDto: UserDto?
)