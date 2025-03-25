package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class AgentChangePasswordResponse(
    @SerializedName("status")
    val status : String,

    @SerializedName("message")
    val message : String
)