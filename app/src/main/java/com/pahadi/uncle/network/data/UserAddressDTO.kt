package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class UserAddressDTO(
    @SerializedName("user_id")
    val user_id : String,

    @SerializedName("id")
    val address_id : String,

    @SerializedName("full_name")
    val full_name : String,

    @SerializedName("mobile")
    val mobile : String,

    @SerializedName("pincode")
    val pincode : String,

    @SerializedName("flat")
    val flat : String,

    @SerializedName("area")
    val area : String,

    @SerializedName("landmark")
    val landmark : String,

    @SerializedName("city")
    val city : String,

    @SerializedName("state")
    val state : String,


)
