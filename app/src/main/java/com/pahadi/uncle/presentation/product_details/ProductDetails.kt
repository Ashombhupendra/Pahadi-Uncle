package com.pahadi.uncle.presentation.product_details

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class ProductDetails(
    val productId: String,
    val userId: String,
    val mainImageUrl: String,
    val priceFormatted: String,
    val title: String,
    val location: String?,
    val description: String,
    val product_district: String,
    val product_condition: String,
    val productunit: String,
    val productcity: String,
    val specification: String,
    val featured: Boolean,
    val username: String,
    val profileImage: String?,
    var wishlist: Boolean,
    val showwishlist: Boolean,
    val ratings: Float,
    val total_rating: Int
) : Parcelable {
    // Constructor to recreate object from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readFloat(),
        parcel.readInt()
    )

    // Write object values to Parcel for storage
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(userId)
        parcel.writeString(mainImageUrl)
        parcel.writeString(priceFormatted)
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeString(product_district)
        parcel.writeString(product_condition)
        parcel.writeString(productunit)
        parcel.writeString(productcity)
        parcel.writeString(specification)
        parcel.writeByte(if (featured) 1 else 0)
        parcel.writeString(username)
        parcel.writeString(profileImage)
        parcel.writeByte(if (wishlist) 1 else 0)
        parcel.writeByte(if (showwishlist) 1 else 0)
        parcel.writeFloat(ratings)
        parcel.writeInt(total_rating)
    }

    // Required method, but usually not used
    override fun describeContents(): Int {
        return 0
    }

    // Companion object that creates instances of ProductDetails from Parcel
    companion object CREATOR : Parcelable.Creator<ProductDetails> {
        override fun createFromParcel(parcel: Parcel): ProductDetails {
            return ProductDetails(parcel)
        }

        override fun newArray(size: Int): Array<ProductDetails?> {
            return arrayOfNulls(size)
        }
    }
}
