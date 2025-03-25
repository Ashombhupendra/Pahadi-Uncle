package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class MessagesResponse(
    @SerializedName("status")
    val hasMessages: Boolean,
    @SerializedName("data")
    val messages: List<MessageDto>
)