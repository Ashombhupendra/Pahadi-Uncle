package com.pahadi.uncle.presentation.seller_information

import android.util.Log
import androidx.lifecycle.*
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AuthRepository
import kotlinx.coroutines.launch
import org.json.JSONArray


enum class NetworkState {
    LOADING_STARTED, LOADING_STOPPED, SUCCESS, FAILED
}
class SellerInformationViewModel(userId: String) : ViewModel() {
    val sellerDetails = liveData {
        Log.d("sellerdetail122 ", "hello")
        when(  val result =  AuthRepository.getSellerDetails(userId)){
            is ResultWrapper.Success ->{
                emit(result.response)
                Log.d("sellerdetail1", result.response.toString())
            }
            is ResultWrapper.Failure ->{
                Log.d("sellerdetailf", result.errorMessage.toString())
            }
        }
    }




    val selleredit = MutableLiveData<NetworkState>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val pincode = MutableLiveData<String>()
    val district = MutableLiveData<String>()
    fun editseller(
        userId: String, userName: String, agentcode: String, phone: String,
        building: String, location: String, landmark: String,
        Pincode: String, state: String, city: String
    )
    {
         viewModelScope.launch {
             selleredit.value = NetworkState.LOADING_STARTED
//              val result = AuthRepository.updateSellerDetails(userId ,"" ,userName ,agentcode,phone,
//                          building, location, landmark, state,city,Pincode)
//
//
//             when(result){
//                 is ResultWrapper.Success ->{
//
//                 }
//                 is ResultWrapper.Failure ->{
//
//                 }

       //      }
         }
    }
//    fun updatebuyerprofile(username : String , email : String , phone : String, picture: Pair<String, ByteArray>? ){
//        val buyer1 = (SharedPrefHelper.user)
//
//        viewModelScope.launch {
//             selleredit.value = NetworkState.LOADING_STARTED
//            val result = AuthRepository.buyerprofileupdate(buyer1.sellerId, buyer1.userId, username,email,phone, picture)
//            when(result){
//                is ResultWrapper.Success ->{
//                    Log.d("Result", result.response.toString())
//
//
//
//                }
//                is ResultWrapper.Failure ->{
//                    Log.d("Result", result.errorMessage.toString())
//
//                }
//
//            }
//        }
//
//    }
}

class SellerInformationViewModelFactory(private val userId: String) :
    ViewModelProvider.NewInstanceFactory() {
//     fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return SellerInformationViewModel(userId) as T
//    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellerInformationViewModel(userId) as T
    }


}