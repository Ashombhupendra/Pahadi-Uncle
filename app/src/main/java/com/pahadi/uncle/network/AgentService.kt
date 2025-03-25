package com.pahadi.uncle.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.network.data.AgentChangePasswordResponse
import com.pahadi.uncle.network.data.AgentLoginResponse
import com.pahadi.uncle.network.data.AgentUserDTO
import com.pahadi.uncle.network.data.NotificationDTO
import com.pahadi.uncle.network.utils.safelyCallApi
import okhttp3.Response
import retrofit2.http.*

interface AgentService {

    @POST("Webservice/agentlogin")
    @FormUrlEncoded
    suspend fun loginAgent(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String

    ): AgentLoginResponse

    @GET
    suspend fun getCounts(
        @Url url: String
    ): JsonObject


    @GET
    suspend fun getAgentUsers(
        @Url url : String
    ) : JsonObject

     @POST("Webservice/updatepassword/{}")
     @FormUrlEncoded
     suspend fun changepassword(
         @Field("user_id") userid : String,
         @Field("password") password : String
     ) : AgentChangePasswordResponse


     @GET("webservice/agentnotification/{agent_id}")
     suspend fun getAgentNotification(
         @Path("agent_id") agent_id : String
     ):List<NotificationDTO>
}
