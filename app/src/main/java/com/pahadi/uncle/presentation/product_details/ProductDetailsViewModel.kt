package com.pahadi.uncle.presentation.product_details

import android.util.Log
import androidx.lifecycle.*
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.repositories.RatingRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch

enum class MediaType {
    IMAGE, VIDEO
}

class ProductDetailsViewModel(private val productId: String) : ViewModel() {
    val interested = MutableLiveData<Boolean>(false)
    val ratingstatus = MutableLiveData<Boolean>(false)
    var booleaninterst :Boolean = false

      val productQuantity = MutableLiveData<String>("Qty: 1")
    val addtoCartPro = MutableLiveData<Boolean>(false)
    fun getRatingStatus (){
        val user_id = SharedPrefHelper.user.userId
        viewModelScope.launch {
            val result = ProductRepository.getRatingStatus(productId, user_id.toString())
            when(result){
                is ResultWrapper.Success ->{
                     ratingstatus.value = result.response
                }
                is ResultWrapper.Failure ->{

                }
            }
        }
     }


    fun getInterestStatus(){

        viewModelScope.launch {
         //   interested.value = ProductRepository.getProductInterestStatus(productId)
            val result = ProductRepository.getProductInterestStatus(productId)
            when(result){
                is ResultWrapper.Success ->{
                     if(result.response == 0){
                         interested.value = false
                     }else{
                         interested.value = true
                     }

                }
                is ResultWrapper.Failure ->{
                  Log.d("error", result.errorMessage)
                }
            }
          //  Log.d("Intrest", ProductRepository.getProductInterestStatus(productId).toString())
        }
    }

    val productMedia: LiveData<List<Pair<MediaType, String>>> = liveData {
        Log.d("productid2222", productId)
        val media = mutableListOf<Pair<MediaType, String>>()
        val images = ProductRepository.getProductImages(productId)
        val video = ProductRepository.getProductVideo(productId)

        images.forEach {
            media.add(MediaType.IMAGE to it)
        }
        video?.let {
            media.add(MediaType.VIDEO to it)
        }

        emit(media.toList())
    }

    fun updateInterest() {
        booleaninterst = true
        viewModelScope.launch {
        val result =  ProductRepository.setInterestStatus(productId, interested.value?.not() ?: false)
            when(result){
                is ResultWrapper.Success ->{
                    log(result.response.toString())
                    interested.value = true
                }
                is ResultWrapper.Failure ->{
                     log(result.errorMessage.toString())
                }
            }

        }

    }
}