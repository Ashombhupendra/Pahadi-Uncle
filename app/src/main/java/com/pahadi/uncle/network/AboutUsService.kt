package com.pahadi.uncle.network

import com.google.gson.JsonArray
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AboutUsService {

    @GET("Webservice/about")
    suspend fun getAboutUs(): JsonArray

    @GET("Webservice/privacypolicy")
    suspend fun getPrivacyPolicy(): JsonArray

    @GET("Webservice/termscondition")
    suspend fun getTermsAndCondition(): JsonArray

    @FormUrlEncoded
    @POST("Webservice/contactus")
    suspend fun submitContactUs(
        @Field("description") description: String,
        @Field("email") email: String,
        @Field("title") title: String
    )
}