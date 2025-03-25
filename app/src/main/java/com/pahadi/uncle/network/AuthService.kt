package com.pahadi.uncle.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.network.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*
data class UserStatus(
    @SerializedName("status")
    val status : String,
    @SerializedName("message")
    val message : String
)
interface  AuthService {




    @GET("Webservice/checkagentcode/{agentcode}")
    suspend fun agentcodevalidation(
        @Path("agentcode") agentcode: String
    ): Boolean


    @FormUrlEncoded
    @POST("Webservice/login")
    suspend fun login(
        @Field("phone") mobileNo: String,
        @Field("uniq_id") deviceId: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("Webservice/updateuniqueid")
    suspend fun updateuniqid(
        @Field("phone") mobileNo: String,
        @Field("uniq_id") deviceId: String,
        @Field("device_id") firebaseId: String
    )

    @FormUrlEncoded
    @POST("Webservice/user")
    suspend fun register(
        @Field("phone") mobileNo: String,
        @Field("device_id") firebaseId: String,
        @Field("username") userName: String,
        @Field("email") email: String?,
        @Field("device_type") deviceType: String,
        @Field("uniq_id") deviceId: String,
        @Field("agentcode") agentCode: String?
    ): RegisterDto

    @FormUrlEncoded
    @POST("Webservice/selleruser")

    suspend fun addSellerDetails(@FieldMap fieldMap: Map<String, String>)




     @Multipart
    @POST("Webservice/selleruser")
    suspend fun updatesellerDetails(
         @Part("profile_id") profileID:  RequestBody,
         @Part("user_id") user_id:  RequestBody,
         @Part("uniq_id") deviceId:  RequestBody,
         @Part("email") email:  RequestBody,
         @Part("username") userName:  RequestBody,
         @Part("building") building :  RequestBody,
         @Part("location") location :  RequestBody,
         @Part("landmark") landmark :  RequestBody,
         @Part("district") district :  RequestBody,
         @Part("state") state :  RequestBody,
         @Part("city") city :  RequestBody,
         @Part("pincode") pincode :  RequestBody,
         @Part picture :MultipartBody.Part
     ) : JsonObject

    @Multipart
    @POST("Webservice/selleruser")
    suspend fun updatesellerDetails_(
        @Part("profile_id") profileID:  RequestBody,
        @Part("user_id") user_id:  RequestBody,
        @Part("uniq_id") deviceId:  RequestBody,
        @Part("email") email:  RequestBody,
        @Part("username") userName:  RequestBody,
        @Part("building") building :  RequestBody,
        @Part("location") location :  RequestBody,
        @Part("landmark") landmark :  RequestBody,
        @Part("district") district :  RequestBody,
        @Part("state") state :  RequestBody,
        @Part("city") city :  RequestBody,
        @Part("pincode") pincode :  RequestBody
    ) : JsonObject



    @Multipart
    @POST("Webservice/selleruser")
    suspend fun buyerprofileUpdate(
        @Part("profile_id") profileID:  RequestBody,
        @Part("user_id") user_id:  RequestBody,
        @Part("username") userName:  RequestBody,
        @Part("phone") phonenumber :  RequestBody,
        @Part("email") email :  RequestBody,
        @Part("uniq_id") deviceId:  RequestBody,
        @Part picture :MultipartBody.Part

    ): JsonObject
    @Multipart
    @POST("Webservice/selleruser")
    suspend fun buyerprofileupdate_(
        @Part("profile_id") profileID:  RequestBody,
        @Part("user_id") user_id:  RequestBody,
        @Part("username") userName:  RequestBody,
        @Part("phone") phonenumber :  RequestBody,
        @Part("email") email :  RequestBody,
        @Part("uniq_id") deviceId:  RequestBody

        ): JsonObject

    @GET("Webservice/seller/{userId}")
    suspend fun getSellerDetails(@Path("userId") userId:String): SellerResponse

    @GET
    suspend fun getPostOffices(@Url url:String) : PinCodeResponse

    @GET("Webservice/usernotification/{user_id}")
    suspend fun getnotification(
        @Path("user_id") user_id:String
    ): List<NotificationDTO>

    @GET("webservice/getuserstatus/{user_id}")
    suspend fun getUserStatus(@Path("user_id") user_id:String) : UserStatus
}
