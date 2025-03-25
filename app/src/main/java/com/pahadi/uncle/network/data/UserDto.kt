package com.pahadi.uncle.network.data


import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("agentcode")
    val agentcode: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("modified")
    val modified: String,
    @SerializedName("notification_status")
    val notificationStatus: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("uniq_id")
    val uniqId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("verify_otp")
    val verifyOtp: String,

    @SerializedName("profile_img")
    val profileImage: String?
)