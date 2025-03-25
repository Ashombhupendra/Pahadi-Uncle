package com.pahadi.uncle.domain.repositories

import com.pahadi.uncle.network.RatingServices
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi

object RatingRepository {
    val ratingservices = getRetrofitService(RatingServices::class.java)



    suspend fun postRating(user_id : String, product_id : String, rating : Int , review : String)
    = safelyCallApi {

        ratingservices.sendRating(user_id, product_id, rating, review)
    }


    suspend fun getRatings(product_id: String, count : Int)= safelyCallApi {
        ratingservices.getRating(product_id, count)
    }
}