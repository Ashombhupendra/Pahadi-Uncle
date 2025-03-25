package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.domain.utils.BASE_URL

data class ChatDto(
    @SerializedName("created")
    val created: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("receiver_id")
    val receiverId: String,
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("username")
    val userName: String?,
    @SerializedName("profile_img")
    val profileImageName: String,
    @SerializedName("product_name")
    val product_name : String,

    @SerializedName("product_id")
    val product_id : String,
) {
    val imageUrl: String?
        get() {
            return if (profileImageName.isNullOrBlank()) {
                null
            } else   "$BASE_URL/uploads/profile/${profileImageName}"
        }

    val nameFormatted: String
        get() = if(userName.isNullOrBlank()) "User" else userName.capitalize()
}