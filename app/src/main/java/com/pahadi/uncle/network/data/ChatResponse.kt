package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("status")
    val hasChat: Boolean,
    @SerializedName("data")
    val chatList: List<ChatDto>
)