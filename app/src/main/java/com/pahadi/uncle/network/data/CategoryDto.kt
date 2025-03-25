package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("category")
    val categoryName: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("icon")
    val iconName: String,
    @SerializedName("id")
    val id: Int
)