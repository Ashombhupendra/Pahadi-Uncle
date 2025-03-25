package com.pahadi.uncle.network.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
data class MessageDto(
    @SerializedName("created")
    val created: String,
    @PrimaryKey @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("receiver_id")
    val receiverId: String,
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("read_status")
    val read_status: String,
    @SerializedName("delivered_status")
    val delivered_status: String,

    @SerializedName("profile_img")
    val profile_img : String




)