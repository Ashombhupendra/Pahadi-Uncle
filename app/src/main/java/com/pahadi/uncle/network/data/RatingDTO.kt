package com.pahadi.uncle.network.data

import com.google.gson.annotations.SerializedName

data class RatingDTO(

                  @SerializedName("id")
                  val id : String,

                  @SerializedName("product_id")
                  val product_id : String,

                  @SerializedName("user_id")
                  val user_id : String,

                  @SerializedName("rating")
                  val rating : Float,

                  @SerializedName("review")
                  val review : String,

                  @SerializedName("created")
                  val created : String,

                  @SerializedName("username")
                  val username : String,

                  @SerializedName("profile")
                  val profile : String
            )
