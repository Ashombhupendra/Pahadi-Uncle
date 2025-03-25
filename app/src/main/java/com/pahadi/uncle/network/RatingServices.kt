package com.pahadi.uncle.network

import com.google.gson.JsonObject
import com.pahadi.uncle.network.data.RatingDTO
import retrofit2.http.*

interface RatingServices {



    //https://www.pahadiuncle.com/api/product/rateproduct
    //POST: user_id, product_id, rating, review
    @POST("product/rateproduct")
    @FormUrlEncoded
    suspend fun sendRating(
        @Field("user_id")user_id : String,
        @Field("product_id")product_id : String,
        @Field("rating")rating : Int,
        @Field("review")review : String
    ): Boolean


    @GET("product/productratings/{product_id}/{count}")
    suspend fun getRating(
         @Path("product_id") product_id : String,
         @Path("count") count : Int

    ):  List<RatingDTO>






}