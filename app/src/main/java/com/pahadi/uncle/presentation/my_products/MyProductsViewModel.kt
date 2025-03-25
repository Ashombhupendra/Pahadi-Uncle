package com.pahadi.uncle.presentation.my_products

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.ERROR_MSG
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.ProductDto
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyProductsViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>(false)
     val stockcomplete = MutableLiveData<Boolean>(false)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log(throwable.message)
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(PahadiUncleApplication.instance, ERROR_MSG, Toast.LENGTH_SHORT).show()
        }
    }



    val approvedProducts = MutableLiveData<List<ProductDto>>()

    val unApprovedProducts = MutableLiveData<List<ProductDto>>()

    fun loadMyProducts() {
        viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
            isLoading.value = true
            approvedProducts.value = ProductRepository.getMyProducts(true)
            unApprovedProducts.value = ProductRepository.getMyProducts(false)
            isLoading.value = false
        }
    }

//    fun deleteProduct(productId: String) = liveData(Dispatchers.Main) {
//        isLoading.value = true
//        emit(ProductRepository.deleteProduct(productId))
//        isLoading.value = false
//    }
    fun  updateAvailablestatus(productID: String, productavailableStatus : String) = liveData(Dispatchers.Main){
                     isLoading.value =true
                 emit(ProductRepository.productavailablestatus(productavailableStatus, productID))
         isLoading.value = false
}
     fun setStockUnstock(productID: String) {
         viewModelScope.launch {
             val result = ProductRepository.setProductStock(productID)
             when(result){
                 is ResultWrapper.Success ->{
                     Log.d("Stock", "true")
                     stockcomplete.value = true

                 }
                 is ResultWrapper.Failure ->{
                     Log.d("Stock", "error")
                 }
             }
         }
     }
}