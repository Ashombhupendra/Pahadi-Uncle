package com.pahadi.uncle.presentation.sell_product

import android.util.Log
import androidx.lifecycle.*
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.DistrictDto
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.Slider.SliderItem
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch

class SellProductViewModel : ViewModel() {
    var productId: String? = null
    val productName = MutableLiveData<String>()
    val productUnit = MutableLiveData<String>()
    val productcity = MutableLiveData<String>()
    val editcategory = MutableLiveData<String>()
    val imagelist = mutableListOf<saveImageData>()

    val editcategoryboolean = MutableLiveData<Boolean>(false)

    val productNameError = MediatorLiveData<String>().apply {
        addSource(productName) {
            this.value = if (it.isEmpty()) "Name cannot be empty" else ""
        }
    }
    val price = MutableLiveData<String>()
    val priceError = MediatorLiveData<String>().apply {
        addSource(price) {
            this.value = if (it.isEmpty()) "Price cannot be Empty" else ""
        }
    }
    var selectedCategoryId = -1

    var selectconditionID = MutableLiveData<String>()
    var selectDistrict = MutableLiveData<String>()



    val description = MutableLiveData<String>()
    val specification = MutableLiveData<String>()

    val images: MutableList<Pair<ByteArray, String>> = mutableListOf()
    var video: Pair<ByteArray, String>? = null

    val sellProductState = MutableLiveData<NetworkState>()
    var error = "An Unexpected Error Occurred"
   // product_condition
    fun addProduct() {
        sellProductState.value = NetworkState.LOADING_STARTED
        viewModelScope.launch {
            val user = SharedPrefHelper.user
            val sellerResponse = AuthRepository.getSellerDetails(user.userId)
            when(sellerResponse){
                is ResultWrapper.Success ->{
                    if (sellerResponse.response.status){
                        Log.d("PRODUCT", user.toString())
                        val result = ProductRepository.addProduct(
                            productName = productName.value ?: "",
                            price = price.value ?: "",
                            productcondition = selectconditionID.value ?:"",
                            categoryId = selectedCategoryId.toString(),
                            district = selectDistrict.value?:"",
                            specification = specification.value ?: "",
                            description = description.value ?: "",
                            unit = productUnit.value ?:"",
                            userId = user.userId,
                            sellerId = sellerResponse.response.sellerDto.profileId,
                            images = images,
                            video = video,
                            productId = productId,
                            city = productcity.value ?:""
                        )

                        sellProductState.value = NetworkState.LOADING_STOPPED

                        when(result){
                            is ResultWrapper.Success ->{
                                sellProductState.value = NetworkState.SUCCESS
                            }
                            is ResultWrapper.Failure ->{
                                sellProductState.value = NetworkState.FAILED
                                temp_showToast(result.errorMessage)
                            }
                        }
                    }else{
                        sellProductState.value = NetworkState.FAILED

                    }
                }
                is ResultWrapper.Failure ->{
                    sellProductState.value = NetworkState.FAILED
                    temp_showToast(sellerResponse.errorMessage)
                }
            }

        }
    }

    fun setDefaultValues(productDto: ProductDto) {
        productName.value = productDto.productName
        price.value = productDto.price
        description.value = productDto.description
        specification.value = productDto.specification
        productId = productDto.id.toString()
        productUnit.value = productDto.unit
        productcity.value  = productDto.city
        selectconditionID.value = productDto.product_condition
        selectDistrict.value = productDto.product_district
        selectedCategoryId = productDto.categoryId.toInt()
        getCategoryname(productDto.categoryId.toInt())
           Log.d("categoryproduct", "${productDto}")

    }

    fun getCategoryname(categoryId : Int) {
        viewModelScope.launch {
            val result = ProductRepository.getCategoryname(categoryId)
            when(result){
                is ResultWrapper.Success ->{
                    editcategory.value = result.response
                    editcategoryboolean.value = true
                    Log.d("resultcategoryname", "${result.response}")
                }
                is ResultWrapper.Failure ->{
                    Log.d("resultcategoryname", "${result.errorMessage}")
                }
            }
        }
    }

    fun getImages() = liveData {
        val images = ProductRepository.getProductImages(productId!!)
        emit(images)
    }

}
