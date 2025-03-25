package com.pahadi.uncle.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.network.data.CategoryDto
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.ERROR_MSG
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.DistrictDto
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.network.data.SLiderImageDto
import com.pahadi.uncle.presentation.home.Slider.SliderAdapter
import com.pahadi.uncle.presentation.home.Slider.SliderItem
import com.pahadi.uncle.presentation.home.categories.CategoryItem
import com.pahadi.uncle.presentation.sell_product.district_list
//import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val categories = MutableLiveData<List<CategoryItem>>()
    val sliderlist1 = MutableLiveData<List<SliderItem>>()
    val districtlists = MutableLiveData<List<district_list>>()
    val cateitem = MutableLiveData<Int>()
    val userID = MutableLiveData<String>()
    val shareid = MutableLiveData<String>()
    val progressboolean = MutableLiveData<Boolean>(true)
    val getfeaturedProductslist = MutableLiveData<List<ProductDto>>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log(throwable.message)
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(PahadiUncleApplication.instance, ERROR_MSG, Toast.LENGTH_SHORT).show()
        }
    }
    fun getProducts(categoryId: Int) =
        ProductRepository.getProducts(categoryId, userID.value.toString()).cachedIn(viewModelScope)

    val isLoading = MutableLiveData<Boolean>(false)
    val sellerproducts = MutableLiveData<List<ProductDto>>()

    fun getFeaturedProduct(categoryId: Int){
        viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
           getfeaturedProductslist.value = ProductRepository.getFeaturedProduct(categoryId, userID.value.toString())

        }

    }

    fun getSellerProduct(user_id : String){
        isLoading.value = true
        viewModelScope.launch {
            val result = ProductRepository.getSellerProduct(user_id)
            when(result){
                is ResultWrapper.Success ->{
                    sellerproducts.value = result.response
                    isLoading.value = false
                }
                is ResultWrapper.Failure ->{
                    isLoading.value = false
                }
            }
        }
    }



    fun getSliderImage (){
            if (sliderlist1.value != null) return
          viewModelScope.launch {
              val imageresult = ProductRepository.getSliderimage()
              when (imageresult) {
                  is ResultWrapper.Success -> {
                      Log.d("RESULT", imageresult.response.toString())
                      val sliderList = mutableListOf<SliderItem>()

                      sliderList.addAll(imageresult.response.map { toslideritem(it) })
                      Log.d("RESULT", imageresult.toString())
                     sliderlist1.value = sliderList

                  }
                  is ResultWrapper.Failure -> {
                      Log.d("RESULT", imageresult.errorMessage)
                  }
              }

            }
      }
    fun getCategories() {
        if(categories.value != null) return

        viewModelScope.launch {
            val result = ProductRepository.getCategories()
           when(result){
               is ResultWrapper.Success ->{
                   val categoryList = mutableListOf<CategoryItem>()
                   categoryList.add(
                           CategoryItem(
                                   -1,
                                   "All",
                                   "$BASE_URL/uploads/category/All-CAtegories-Icon.png"
                           )
                   )
                   categoryList.addAll(result.response.map { toCategoryItem(it) })
                   categories.value = categoryList
               }
               is ResultWrapper.Failure ->{
                   Log.d("Pahadi" , result.errorMessage)
               }
           }
        }


    }
    fun getDIstrict (){
        viewModelScope.launch {
            val result = ProductRepository.getdistricts()
            when(result){
                is ResultWrapper.Success -> {
                    val districtlistssss = mutableListOf<district_list>()

                    districtlistssss.addAll(result.response.map { toDistrictItem(it) })

                    districtlists.value = districtlistssss

                }
                is ResultWrapper.Failure -> {

                }

            }
        }
    }
    private fun toCategoryItem(categoryDto: CategoryDto) = CategoryItem(
        categoryDto.id,
        categoryDto.categoryName,
        "$BASE_URL/uploads/category/${categoryDto.iconName}"
    )
    private fun toslideritem(sLiderImageDto: SLiderImageDto) =SliderItem(
        sLiderImageDto.sliderImaggeUrl,sLiderImageDto.id,sLiderImageDto.created,sLiderImageDto.bannerimage
    )
    private fun toDistrictItem(districtdto : DistrictDto) = district_list(
        districtdto.d_district
    )

}
