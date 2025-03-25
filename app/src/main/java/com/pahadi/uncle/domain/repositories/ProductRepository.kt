package com.pahadi.uncle.domain.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.liveData
import com.google.gson.JsonSyntaxException
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.ProductService
import com.pahadi.uncle.network.data.CategoryDto
import com.pahadi.uncle.network.data.DistrictDto
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.network.data.SLiderImageDto
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

object ProductRepository {
    private const val PRODUCTS_PAGE_SIZE = 3
    private val productService = getRetrofitService(ProductService::class.java)
    var categories = listOf<CategoryDto>()

    var sliderimage = listOf<SLiderImageDto>()
    var districts = listOf<DistrictDto>()




    suspend fun  getRatingStatus(productId: String, userId: String) = safelyCallApi {
         productService.checkratingStatus(product_id = productId, user_id = userId)
    }

    suspend fun getSellerProduct(userId: String) = safelyCallApi {
        productService.getSellerProduct(userId)
    }

    suspend fun saveWishList(userId: String, productId: String) = safelyCallApi {
        Log.d("wishlist1", "$userId : $productId")
               productService.saveWishList(userId, productId)
    }

    suspend fun getwishList(userId: String) = safelyCallApi {
        productService.getWishlist(-1, 0, userId)
    }


     suspend fun getsingleproduct(productId: String) = safelyCallApi {
         productService.getsingleproduct(productId)
     }
    suspend fun getdistricts() = safelyCallApi {
        districts = productService.getDIstrict()

        districts
    }

     suspend fun getSliderimage() = safelyCallApi {
         sliderimage = productService.sliderimageurl()

         sliderimage
     }

    suspend fun getCategories() = safelyCallApi {
        categories = productService.getCategories()

        categories
    }

    fun getProducts(categoryId: Int, userId: String) = Pager(
        config = PagingConfig(
            pageSize = PRODUCTS_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(productService, categoryId, userId)
        }
    ).flow

    fun searchProducts(query: String, district: String) = Pager(

        config = PagingConfig(
            pageSize = PRODUCTS_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchProductsPagingSource(productService, query,district)
        }

    ).liveData

    suspend fun addProduct(
        productName: String,
        price: String,
        categoryId: String,
        productcondition : String,
        unit : String,
        city: String,
        district : String,
        specification: String,
        description: String,
        userId: String,
        sellerId: String,
        images: List<Pair<ByteArray, String>>,
        video: Pair<ByteArray, String>?,
        productId: String?
    ): ResultWrapper<out Unit> = safelyCallApi {

         checkSellerProfileExists()

        val createProductResponse = productService.addProduct(
            mapOf(
                "product_name" to productName,
                "price" to price,
                "category_id" to categoryId,
                "product_condition" to productcondition,
                "specification" to specification,
                "description" to description,
                "unit" to unit,
                "city" to city,
                "user_id" to userId,
                "profile_id" to sellerId,
                "stock" to "0",
                "product_id" to (productId ?: ""),
                "district" to district
            )
        )

        val imageMultipartList = images.map {
            val imageRequestBody = it.first.toRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("profile_img[]", it.second, imageRequestBody)
        }
        Log.d("Result n" , imageMultipartList.toString())
        val videoRequestBody = video?.first?.toRequestBody("video/*".toMediaType())
        val videoMultipart = videoRequestBody?.let {
            MultipartBody.Part.createFormData(
                "profile_vid",
                video.second,
                videoRequestBody
            )
        }
        val productIdMultipartBody = MultipartBody.Part.createFormData(
            "product_id",
            createProductResponse.productId.toString()
        )
        productService.uploadMedia(
            productId = productIdMultipartBody,
            images = imageMultipartList,
            video = videoMultipart
        )
    }

    private suspend fun checkSellerProfileExists() {
        val user = SharedPrefHelper.user
        val sellerResponse = AuthRepository.getSellerDetails(user.userId)
        when(sellerResponse){
            is ResultWrapper.Success ->{
                if (sellerResponse.response.sellerDto.profileId == "-1") {
                    AuthRepository.addSellerDetails(
                        userId = user.userId,
                        userName = user.userName,
                        agentCode = "Default agent code",
                        phone = user.phoneNumber,
                        buildingNumber = "building number",
                        location = "location",
                        landmark = "landmark",
                        state = "state",
                        city = "city",
                        pinCode = "pinCode",
                        district = "district"
                    )



                    //saves user locally
                    SharedPrefHelper.user = UserEntity(
                        userId = user.userId,
                        sellerId = sellerResponse.response.sellerDto.profileId,
                        userName = user.userName,
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        profile_image = user.profile_image
                    )
                }
            }
            is ResultWrapper.Failure ->{
                log("${sellerResponse.errorMessage}")
            }
        }

    }

    suspend fun getProductImages(productId: String): List<String> {
        return try {
            val productDtoList = productService.getProductImages(productId)
            Log.d("Productdetails", productId.toString())
            productDtoList.map {
                "$BASE_URL/uploads/product/${it.productImageName}"
            }
        } catch (ex: Exception) {
            emptyList()
        }catch (ex: HttpException) {
            emptyList()
        }catch (ex : UnknownHostException){
            emptyList()

        } catch (ex: JsonSyntaxException) {
            emptyList()
        }catch (ex : SocketTimeoutException){
            emptyList()
        }catch (ex : UnknownServiceException){
            emptyList()
        }catch (ex : IOException){
            emptyList()
        }
    }

    suspend fun getProductImageslist(productId: String) = safelyCallApi {
        productService.getProductImages(productId)
    }

    suspend fun getProductVideo(productId: String): String? {
        return try {
            val videoDtoList = productService.getProductVideo(productId)
            if (videoDtoList.isEmpty()) null
            else "$BASE_URL/uploads/product/${videoDtoList[0].productVideoName}"
        } catch (ex: HttpException) {
            null
        }catch (ex : UnknownHostException){
           null

        } catch (ex: Exception) {
           null
        }catch (ex: JsonSyntaxException) {
            null
        }catch (ex : SocketTimeoutException){
            null
        }catch (ex : UnknownServiceException){
            null
        }catch (ex : IOException){
            null
        }
    }
//
//    suspend fun deleteProduct(productId: String) = safelyCallApi {
//        productService.deleteProduct(productId)
//    }

    suspend fun reportProduct(productId: String, reportDescription: String) {
        val userId = SharedPrefHelper.user.userId
        productService.reportProduct(productId, userId, reportDescription)
    }

    //getFeatured Product
    suspend fun getFeaturedProduct(categoryId: Int, userId: String): List<ProductDto> = withContext(Dispatchers.IO){
        try {
            productService.getFeaturedProducts(categoryId, 0, userId )
        } catch (ex: HttpException) {
            if (ex.code() == 404)
                emptyList<ProductDto>()
            else throw ex
        }catch (ex : UnknownHostException){
            emptyList<ProductDto>()
            throw ex

        } catch (ex: Exception) {
            emptyList<ProductDto>()
            throw ex
        }catch (ex: JsonSyntaxException) {
            emptyList<ProductDto>()
            throw ex
        }catch (ex : SocketTimeoutException){
            emptyList<ProductDto>()
            throw ex
        }catch (ex : UnknownServiceException){
            emptyList<ProductDto>()
            throw ex
        }catch (ex : IOException){
            emptyList<ProductDto>()
            throw ex
        }

    }

    suspend fun getMyProducts(approved: Boolean): List<ProductDto> = withContext(Dispatchers.IO) {
        val userId = SharedPrefHelper.user.userId
        try {
            if (approved)
                productService.getMyApprovedProducts(userId)
            else productService.getMyUnApprovedProducts(userId)
        } catch (ex: HttpException) {
            if (ex.code() == 404)
                emptyList<ProductDto>()
            else throw ex
        }catch (ex : UnknownHostException){
            emptyList<ProductDto>()
             throw ex

        } catch (ex: Exception) {
            emptyList<ProductDto>()
             throw ex
        }catch (ex: JsonSyntaxException) {
            emptyList<ProductDto>()
             throw ex
        }catch (ex : SocketTimeoutException){
            emptyList<ProductDto>()
             throw ex
        }catch (ex : UnknownServiceException){
            emptyList<ProductDto>()
             throw ex
        }catch (ex : IOException){
            emptyList<ProductDto>()
             throw ex
        }
    }

    suspend fun setInterestStatus(productId: String, interested: Boolean) = safelyCallApi {
        val userId = SharedPrefHelper.user.userId
     //  val sellerResponse = AuthRepository.getSellerDetails(userId)
      // val sellerId = sellerResponse.sellerDto.profileId
        Log.d("Intrest", productId+":"+userId+"seller:")
        if (interested) {
            productService.showInterest(userId = userId, productId = productId)
         //   Log.d("Intrest1", productId+":"+userId+"seller:")

        } else {
            productService.removeInterest(userId, productId)
         //   Log.d("Intrest2", productId+":"+userId+"seller:")


        }
    }

    suspend fun  productavailablestatus(avalaiblestatus : String, productId: String) = safelyCallApi {
                 productService.updateavailablestatus(avalaiblestatus,productId)
    }

    suspend fun getProductInterestStatus(productId: String)= safelyCallApi{
        val userId = SharedPrefHelper.user.userId
        productService.checkInterest(userId, productId)

    }

    suspend fun setProductStock(productId: String) = safelyCallApi {
          productService.setProductStock(productId)
    }

    suspend fun getCategoryname(categoryId: Int) = safelyCallApi {
        productService.getCategoryname(categoryId)
    }
}
