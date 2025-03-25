package com.pahadi.uncle.network.data


import com.google.gson.annotations.SerializedName

data class PostOffice(
    @SerializedName("Block")
    val block: String,
    @SerializedName("BranchType")
    val branchType: String,
    @SerializedName("Circle")
    val circle: String,
    @SerializedName("Country")
    val country: String,
    @SerializedName("DeliveryStatus")
    val deliveryStatus: String,
    @SerializedName("Description")
    val description: Any,
    @SerializedName("District")
    val district: String,
    @SerializedName("Division")
    val division: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Pincode")
    val pincode: String,
    @SerializedName("Region")
    val region: String,
    @SerializedName("State")
    val state: String
)