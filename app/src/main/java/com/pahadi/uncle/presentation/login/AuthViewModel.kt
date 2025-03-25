package com.pahadi.uncle.presentation.login

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.utils.ERROR_MSG
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.notifications.NotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class NetworkState {
    LOADING_STARTED, LOADING_STOPPED, SUCCESS, FAILED
}

class AuthViewModel : ViewModel() {
    val mobileNo = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val agentCode = MutableLiveData<String>()
    val buildingNumber = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val landmark = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val district = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val pinCode = MutableLiveData<String>()
    val stoken =    MutableLiveData<String>()
    val Email =    MutableLiveData<String>()


    lateinit var authType: AuthType
    lateinit var otp: String
    lateinit var mobile_no : String

    val isMobileNoValid = MediatorLiveData<Boolean>().apply {
        addSource(mobileNo) {
            val mobile = mobileNo.value

            this.value = mobile != null && mobile.length == 10 && mobile.isDigitsOnly()
        }
    }

    val isUserNameValid = MediatorLiveData<Boolean>().apply {
        addSource(userName) {
            this.value = !userName.value.isNullOrBlank()
        }
    }

    val loginNetworkState = MutableLiveData<NetworkState>()
    val registerNetworkState = MutableLiveData<NetworkState>()
    val buyerregisterNetworkState = MutableLiveData<NetworkState>()
    var errorMessage = ERROR_MSG
    var erroragentcode = MutableLiveData<String?>(null)
    val agentcodevalid = MutableLiveData<Boolean>(false)


    fun getAgentCodeVAlidation(){
        viewModelScope.launch {
            val result = AuthRepository.getAgentcodevalidation(agentCode.value.toString())
            when(result){
                is ResultWrapper.Success ->{

                    if (!result.response){
                        erroragentcode.value = "Enter Valid AgentCode"
                        agentcodevalid.value = false
                    }else{
                        agentcodevalid.value = true
                        erroragentcode.value = null
                    }
                }
                is ResultWrapper.Failure ->{

                    Log.d("agentcodevalidation", result.errorMessage)
                }
            }
        }
    }

    fun registerBUyerProfile(){
        viewModelScope.launch {
            buyerregisterNetworkState.value = NetworkState.LOADING_STARTED
            val res = AuthRepository.registerbuyer(
                userName = userName.value ?: "",
                mobileNo = mobileNo.value ?: "",
                    email = Email.value ?: "",
                agentCode = agentCode.value ?: "",
                token = stoken.value ?: ""
            )
            buyerregisterNetworkState.value = NetworkState.LOADING_STOPPED
            when (res) {
                is ResultWrapper.Success -> {
                    buyerregisterNetworkState.value = NetworkState.SUCCESS
                }
                is ResultWrapper.Failure -> {
                    errorMessage = res.errorMessage
                    Log.d("SELLER",res.errorMessage)
                    buyerregisterNetworkState.value = NetworkState.FAILED
                }
            }

        }
    }

    fun updateuniqid(){
        viewModelScope.launch {
            val mobile = mobileNo.value
            val result = AuthRepository.updateuniqid(mobile.toString(), token = stoken.value.toString())

            when(result){
               is ResultWrapper.Success ->{
                         Log.d("updateuniq", result.response.toString())
               }
                is ResultWrapper.Failure ->{
                    Log.d("updateuniq", result.errorMessage)
                }
            }


        }
    }

    fun login() {
        if (!PahadiUncleApplication.instance.isConnectedToInternet()){
              Toast.makeText(PahadiUncleApplication.instance,"No Internet Connection !", Toast.LENGTH_SHORT).show()
        }else{
            Log.d("stoken", stoken.value.toString())
            viewModelScope.launch {
            val mobile = mobileNo.value

            if (mobile != null) {
                loginNetworkState.value = NetworkState.LOADING_STARTED
                val result = AuthRepository.login(mobile)
                loginNetworkState.value = NetworkState.LOADING_STOPPED
                when (result) {
                    is ResultWrapper.Success -> {
                        val loginResponse = result.response
//                        Toast.makeText(PahadiUncleApplication.instance, "Your OTP: ${loginResponse.otp}", Toast.LENGTH_SHORT).show()
                        Log.d("loginres", loginResponse.toString())
                        when (loginResponse.message) {
                            "matched" -> {
                                otp = result.response.otp
                                authType = AuthType.EXISTING_USER
                                Log.d("Users_m", result.response.toString())
                                //   temp_showToast(otp)
                            }
                            "unmatched" -> {
                                Log.d("Users_m", result.response.toString())
                                otp = result.response.otp
                                authType = AuthType.DIFFERENT_UNIQ_ID
                                //   temp_showToast(otp)

                            }
                            "Phone Not found." -> {
                                otp = result.response.otp
                                authType = AuthType.NEW_USER
                                // temp_showToast(otp)
                            }
                            "No device found." ->
                                authType = AuthType.DIFFERENT_DEVICE
                        }
                        loginNetworkState.value = NetworkState.SUCCESS
                    }
                    is ResultWrapper.Failure -> {
                        errorMessage = result.errorMessage
                        loginNetworkState.value = NetworkState.SUCCESS
                    }
                }
            }
        }
        }
    }

    fun register() {
        viewModelScope.launch {
            registerNetworkState.value = NetworkState.LOADING_STARTED
            val result = AuthRepository.register(
                userName = userName.value ?: "",
                mobileNo = mobileNo.value ?: "",
                agentCode = agentCode.value ?: "",
                buildingNumber = buildingNumber.value ?: "",
                location = location.value ?: "",
                landmark = landmark.value ?: "",
                state = state.value ?: "",
                city = city.value ?: "",
                district = district.value ?:"",
                pinCode = pinCode.value ?: "452001",
                token = stoken.value ?: ""
            )
            registerNetworkState.value = NetworkState.LOADING_STOPPED
            when (result) {
                is ResultWrapper.Success -> {
                    registerNetworkState.value = NetworkState.SUCCESS
                }
                is ResultWrapper.Failure -> {
                    errorMessage = result.errorMessage
                    Log.d("SELLER",result.errorMessage)
                    registerNetworkState.value = NetworkState.FAILED
                }
            }
        }
    }

    private suspend fun temp_showToast(text: String) = withContext(Dispatchers.Main) {
        Toast.makeText(
            PahadiUncleApplication.instance,
            text,
            Toast.LENGTH_LONG
        ).show() //todo remove this toast
    }

}
