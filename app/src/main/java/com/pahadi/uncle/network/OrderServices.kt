package com.pahadi.uncle.network

import com.google.gson.JsonObject
import com.pahadi.uncle.network.data.MyCartDTO
import com.pahadi.uncle.network.data.OrderHistoryDTO
import com.pahadi.uncle.network.data.UserAddressDTO
import retrofit2.http.*

interface OrderServices {

    @GET("ecommerce/myorders/{user_id}")
    suspend fun getOrderHistory(
        @Path("user_id") user_id : String
    ): List<OrderHistoryDTO>


    @GET("ecommerce/address/{user_id}")
    suspend fun getAddress(
        @Path("user_id") user_id : String
    ): List<UserAddressDTO>

    @GET("ecommerce/deleteaddress/{address_id}")
    suspend fun deleteAddress(
        @Path("address_id") address_id : String
    ): JsonObject

    @GET("ecommerce/cart/{user_id}")
    suspend fun getMyCart(
        @Path("user_id") user_id : String
    ): List<MyCartDTO>

    //https://www.pahadiuncle.com/api/ecommerce/deletecart/{cart_item_id}
    @GET("ecommerce/deletecart/{cart_item_id}")
    suspend fun deleteMycart(
        @Path("cart_item_id") cart_item_id : String
    ): JsonObject


  /*  https://www.pahadiuncle.com/api/ecommerce/transaction
    POST : user_id, payment_mode, payment_id, products, order_amt, address_id*/

    @POST("ecommerce/transaction")
    @FormUrlEncoded
    suspend fun SubmitTransation(
        @Field("user_id") user_id: String,
        @Field("payment_mode") payment_mode :String,
        @Field("payment_id") payment_id : String,
        @Field("products") products : String,
        @Field("order_amt") order_amt : String,
        @Field("address_id") address_id : String,
    )

    @POST("ecommerce/cart")
    @FormUrlEncoded
    suspend fun AddtocardProduct(
        @Field("user_id") user_id: String,
        @Field("product_id") product_id :String,
        @Field("quantity") quantity : String
    )

    @POST("ecommerce/address")
    @FormUrlEncoded
    suspend fun addAddress(
        @Field("user_id") user_id: String,
        @Field("full_name") full_name: String,
        @Field("mobile") phone: String,
        @Field("pincode") pincode: String,
        @Field("flat") house_number: String,
        @Field("area") area: String,
        @Field("landmark") landmark: String,
        @Field("city") city: String,
        @Field("state") state: String,

    ): JsonObject
}