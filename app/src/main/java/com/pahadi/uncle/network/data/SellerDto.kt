package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.domain.utils.BASE_URL

data class SellerDto(
    @SerializedName("agentcode")
    val agentCode: String,
    @SerializedName("building")
    val building: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("landmark")
    val landmark: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("modified")
    val modified: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("pincode")
    val pinCode: String,
    @SerializedName("profile_id")
    val profileId: String,
    @SerializedName("state")
    var state: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("profile_img")
    val profileImage: String?
) {
    val profileUrl: String?
        get() = if(profileImage.isNullOrBlank()) null else "$BASE_URL/uploads/profile/${profileImage}"

    val fullLocation
            get() = "$building, $location, $landmark, $city, $state"
}
