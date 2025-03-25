package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class DistrictDto (

    @SerializedName("id")
    val d_id: String,
    @SerializedName("district")
    val d_district: String,
    @SerializedName("created")
    val d_createddate: String
)