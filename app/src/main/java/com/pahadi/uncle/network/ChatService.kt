package com.pahadi.uncle.network

import com.pahadi.uncle.network.data.ChatAlertResponse
import com.pahadi.uncle.network.data.ChatResponse
import com.pahadi.uncle.network.data.MessagesResponse
import retrofit2.http.*

interface ChatService {

    @GET("Chat/chatlist/{userId}")
    suspend fun getChatList(@Path("userId") userId: String): ChatResponse

    @GET("Chat/chatmessage/{senderId}/{receiverId}/{product_id}/{offset}")
    suspend fun getMessages(
        @Path("senderId") senderId: String,
        @Path("receiverId") receiverId: String,
        @Path("offset") offset: Int,
        @Path("product_id")product_id : String
    ): MessagesResponse

    @FormUrlEncoded
    @POST("Chat/Chat")
    suspend fun sendMessage(
        @Field("sender_id") senderId: String,
        @Field("receiver_id") receiverId: String,
        @Field("message") message: String,
        @Field("product_id") product_id: String

    )

    @GET("Chat/chatbytime/{time}/{senderId}/{receiverId}/{product_id}/0")
    suspend fun getMessagesAfter(
        @Path("time") time: String,
        @Path("senderId") senderId: String,
        @Path("receiverId") receiverId: String,
        @Path("product_id")product_id : String
    ): MessagesResponse

    @FormUrlEncoded
    @POST("Chat/block")
    suspend fun chatBlock(
        @Field("sender_id") senderId: String,
        @Field("receiver_id") receiverId: String
    )

    @FormUrlEncoded
    @POST("webservice/pushnotify")
    suspend fun chatalert(
        @Field("user_id") userId: String
    ) : ChatAlertResponse
}
