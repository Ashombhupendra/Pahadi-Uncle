package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class AgentUserDTO(
    @SerializedName("username")
    val userName: String,

    @SerializedName("email")
    val email : String ,

    @SerializedName("phone")
    val phone : String,

    @SerializedName("created")
    val date : String,

    val userliveproducts : String
)