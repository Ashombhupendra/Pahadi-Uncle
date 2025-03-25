package com.pahadi.uncle.presentation.my_orders

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.OrderHistoryRepository
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.MyCartDTO
import com.pahadi.uncle.network.data.OrderHistoryDTO
import com.pahadi.uncle.network.data.UserAddressDTO
import com.pahadi.uncle.presentation.MainActivity
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch

/*[{"product_id":19, "quantity":6}, {"product_id":118, "quantity":2}]*/

data class SelectProdcutDTO(
    val  product_id : String,

    val quantity : String
)

class OrderViewModel : ViewModel() {
    val orderHistoryList = MutableLiveData<List<OrderHistoryDTO>>()
    val userAddressList = MutableLiveData<List<UserAddressDTO>>()
    val mycartlist = MutableLiveData<List<MyCartDTO>>()

    val selectProductlist = mutableListOf<SelectProdcutDTO>()
    val orderamount =  MutableLiveData<String>()
    val selectedaddress =  MutableLiveData<String>()
    val paymentmode =  MutableLiveData<String>("COD")
    val paymentID =  MutableLiveData<String>("")





    val myPlaceOrderPrice = MutableLiveData<Int>(0)
    //for addding address
    val afullname = MutableLiveData<String>()
    val aphone = MutableLiveData<String>()
    val apincode = MutableLiveData<String>()
    val ahouseNumber = MutableLiveData<String>()
    val aarea = MutableLiveData<String>()
    val alandmark = MutableLiveData<String>()
    val acity = MutableLiveData<String>()
    val astate = MutableLiveData<String>()

    val addAddressresponse = MutableLiveData<NetworkState>()





//    POST : user_id, payment_mode, payment_id, products, order_amt, address_id*/


    fun  doTransaction(product :String){
        viewModelScope.launch {
            val result = OrderHistoryRepository.addTransaction(
                payment_mode = paymentmode.value.toString(),
                payment_id = paymentID.value.toString(),
                products = product,
                order_amt = myPlaceOrderPrice.value.toString(),
                address_id = selectedaddress.value.toString()
            )
            when(result){
                is ResultWrapper.Success ->{
                    // Close current activity
//                    val intent = Intent(PahadiUncleApplication.instance, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    PahadiUncleApplication.instance.startActivity(intent)
                    temp_showToast("Order Placed Successfully")
                }
                is ResultWrapper.Failure -> {
                    temp_showToast("${result.errorMessage}")
                }
            }
        }
    }

    fun addAddress(){
         addAddressresponse.value = NetworkState.LOADING_STARTED
        viewModelScope.launch {
            val result = OrderHistoryRepository.addAddress(
                full_name = afullname.value.toString(),
                phone = aphone.value.toString(),
                pincode = apincode.value.toString(),
                house_number = ahouseNumber.value.toString(),
                area = aarea.value.toString(),
                landmark = alandmark.value.toString(),
                city = acity.value.toString(),
                state = astate.value.toString()
            )
            addAddressresponse.value = NetworkState.LOADING_STOPPED
            when(result){
                is ResultWrapper.Success ->{
                    addAddressresponse.value = NetworkState.SUCCESS
                }
                is ResultWrapper.Failure ->{
                    Log.d("AddAddressErr", "Err - ${result.errorMessage}")
                    addAddressresponse.value = NetworkState.FAILED
                }

            }
        }
    }

    fun getAddress(){
        viewModelScope.launch {
            val result = OrderHistoryRepository.getAddress()
            when(result){
                is ResultWrapper.Success ->{
                    val list = mutableListOf<UserAddressDTO>()
                    list.addAll(result.response.map { it })
                    userAddressList.value = list
                }
                is ResultWrapper.Failure ->{
                    Log.d("Orderhistory", result.errorMessage)
                    temp_showToast(result.errorMessage)
                }
            }
        }
    }

    fun getMyCart(){
        viewModelScope.launch {
            val result = OrderHistoryRepository.getMyCart()
            Log.d("Orderhistory", result.toString())
            when(result){
                is ResultWrapper.Success ->{
                    val list = mutableListOf<MyCartDTO>()
                    list.addAll(result.response.map { it })
                    mycartlist.value = list
                }
                is ResultWrapper.Failure ->{
                    Log.d("Orderhistory", result.errorMessage)
                    temp_showToast(result.errorMessage)
                }
            }
        }
    }

    fun getOrderHistory(){
        viewModelScope.launch {
            val result = OrderHistoryRepository.getOrderHistory()
            when(result){
                is ResultWrapper.Success ->{
                     val list = mutableListOf<OrderHistoryDTO>()
                    list.addAll(result.response.map { it })
                    orderHistoryList.value = list
                }
                is ResultWrapper.Failure ->{
                    Log.d("Orderhistory", result.errorMessage)
                    temp_showToast(result.errorMessage)
                }
            }
        }
    }
}