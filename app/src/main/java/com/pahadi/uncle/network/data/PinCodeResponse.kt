package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class PinCodeResponse(
    @SerializedName("Message")
    val message:String,
    @SerializedName("Status")
    val status:String,
    @SerializedName("PostOffice")
    val postOffices: List<PostOffice>
)