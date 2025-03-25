package com.pahadi.uncle.domain.repositories

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.UserEntityMapper
import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.utils.safelyCallApi
import com.pahadi.uncle.domain.utils.UniqueDeviceId
import com.pahadi.uncle.network.AuthService
import com.pahadi.uncle.network.data.AddressDetails
import com.pahadi.uncle.network.data.NotificationDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


object AuthRepository {
    private val authService = getRetrofitService(AuthService::class.java)
    var notificationlist = listOf<NotificationDTO>()



    suspend fun getAgentcodevalidation(agentCode: String) = safelyCallApi {
          authService.agentcodevalidation(agentCode)
    }

    suspend fun getNotification(userId: String) = safelyCallApi {
        notificationlist=  authService.getnotification(userId)

        notificationlist
    }

    suspend fun getUserStatus(userId: String) = safelyCallApi {
          authService.getUserStatus(userId)
    }

    suspend fun updateuniqid(mobileNo: String, token : String) = safelyCallApi {
         val response =  authService.updateuniqid(mobileNo,UniqueDeviceId.getUniqueId(),token)

        response
    }

    suspend fun registerbuyer(  userName: String,
                                mobileNo: String,
                                email: String?,
                                agentCode: String?,
                                token : String
    ) : ResultWrapper<out Unit> = safelyCallApi {
       val res= authService.register(
            firebaseId = token, //todo setup firebase and change this code
            deviceType = "android",
            deviceId = UniqueDeviceId.getUniqueId(),
            userName = userName,
               email = email,
            mobileNo = mobileNo,
            agentCode = agentCode
        )
        val respo = res.userDto
        respo?.let {
            SharedPrefHelper.user = UserEntity(
                respo.userId,
                "-1",
                respo.username,
                    respo.email,
                respo.phone,
                respo.profileImage!!
            )
        }
    }

    suspend fun login(mobileNo: String) = safelyCallApi {
        val loginResponse = authService.login(mobileNo, UniqueDeviceId.getUniqueId())
        val userDto = loginResponse.userDto
        userDto?.let {
            SharedPrefHelper.user = UserEntity(
                userDto.userId,
                "-1",
                userDto.username,
                    userDto.email,
                userDto.phone,
                userDto.profileImage!!

            )
        }
        loginResponse
    }

    suspend fun register(
        userName: String,
        mobileNo: String,
        agentCode: String?,
        buildingNumber: String,
        location: String,
        landmark: String,
        state: String,
        city: String,
        district: String,
        pinCode: String,
        token : String
    ): ResultWrapper<out Unit> = safelyCallApi {
        //adds basic data of user


       val user = SharedPrefHelper.user
        //adds location related details
        addSellerDetails(
            userId = user.userId,
            userName = user.userName,
            agentCode = agentCode ?: "",
            phone = user.phoneNumber,
            buildingNumber = buildingNumber,
            location = location,
            landmark = landmark,
            state = state,
            city = city,
             district = district,
            pinCode = pinCode
        )

        //fetches seller id of the newly created user along with other seller details.
        val sellerResponse = getSellerDetails(user.userId)


        //saves user locally
     //   SharedPrefHelper.user = UserEntityMapper.toEntity(userDto, sellerResponse.sellerDto)
    }
    //Buyer profile update
    suspend fun buyerprofileupdate(
        profileID: String,
        userId: String,
        userName: String,
        email: String,
        phone: String,
        picture : Pair<String, ByteArray>?

    ): ResultWrapper<out Any> = safelyCallApi {
       val pictureMultipart = picture?.run {
            val pictureRequestBody = this.second.toRequestBody("image/*".toMediaType())
           MultipartBody.Part.createFormData("profile_img", this.first, pictureRequestBody)

       }
        if (pictureMultipart != null) {
            authService.buyerprofileUpdate(profileID = profileID.toRequestBody(), user_id =  userId.toRequestBody(),
                userName = userName.toRequestBody(), email = email.toRequestBody(),phonenumber = phone.toRequestBody(),
                deviceId = UniqueDeviceId.getUniqueId().toRequestBody(), picture = pictureMultipart
            )
            Log.d("Result m" , "this" + pictureMultipart)
        }
            Log.d("Result n", "this")
            authService.buyerprofileupdate_(profileID = profileID.toRequestBody(), user_id = userId.toRequestBody(),
                userName = userName.toRequestBody(),
                email = email.toRequestBody(),
                phonenumber = phone.toRequestBody(),
                deviceId = UniqueDeviceId.getUniqueId().toRequestBody()
            )

    }

    suspend fun getSellerDetails(userId: String) = safelyCallApi {
      val seller =   authService.getSellerDetails(userId)

        seller
    }

    suspend fun addSellerDetails(
        userId: String,
        userName: String,
        agentCode: String,
        phone: String,
        buildingNumber: String,
        location: String,
        landmark: String,
        state: String,
        city: String,
        district : String,
        pinCode: String

    ) {
        val fieldMap = mapOf(
            "user_id" to userId,
            "username" to userName,
            "phone" to phone,
            "agentcode" to agentCode,
            "building" to buildingNumber,
            "landmark" to landmark,
            "state" to state,
            "location" to location,
            "city" to city,
            "district" to district,
            "pincode" to pinCode   //175
        )
        authService.addSellerDetails(fieldMap)
    }


    //for profile update
    suspend fun updateSellerDetails(
        userId: String,
        profileID : String,
        userName: String,
        email: String,
        buildingNumber: String,
        location: String,
        landmark: String,
        state: String,
        district: String,
        city: String,
        pinCode: String,
        picture : Pair<String, ByteArray>?

    ): ResultWrapper<out Any> = safelyCallApi {
        val pictureMultipart = picture?.run {
            val pictureRequestBody = this.second.toRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("profile_img", this.first, pictureRequestBody)
        }

        if (pictureMultipart != null) {
            authService.updatesellerDetails(profileID = profileID.toRequestBody(),user_id = userId.toRequestBody(),deviceId = UniqueDeviceId.getUniqueId().toRequestBody(),
                email = email.toRequestBody(), userName =   userName.toRequestBody(),building = buildingNumber.toRequestBody(),location = location.toRequestBody(),
                landmark = landmark.toRequestBody(),state = state.toRequestBody(), city = city.toRequestBody(),pincode = pinCode.toRequestBody() ,picture = pictureMultipart,district = district.toRequestBody())
        }
        Log.d("ditrict", district.toString())
        authService.updatesellerDetails_(profileID = profileID.toRequestBody(),user_id = userId.toRequestBody(),deviceId = UniqueDeviceId.getUniqueId().toRequestBody(),
            email = email.toRequestBody(), userName =   userName.toRequestBody(),building = buildingNumber.toRequestBody(),location = location.toRequestBody(),
            landmark = landmark.toRequestBody(),district = district.toRequestBody(),state = state.toRequestBody(), city = city.toRequestBody(),pincode = pinCode.toRequestBody() )

    }

    suspend fun getPostOffice(pinCode: String): AddressDetails? {
        if (pinCode.isBlank()) return null
        val url = "http://www.postalpincode.in/api/pincode/${pinCode}"
        val response = authService.getPostOffices(url)
        if (response.postOffices.isNullOrEmpty()) return null
        val city = response.postOffices[0].circle
        val state = response.postOffices[0].state
        val district = response.postOffices[0].district
        val gpoList = response.postOffices.map { it.name }
        return AddressDetails(city, state, district,gpoList )
    }
}
