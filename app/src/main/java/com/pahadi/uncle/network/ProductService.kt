package com.pahadi.uncle.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.pahadi.uncle.network.data.*
import okhttp3.MultipartBody
import retrofit2.http.*


interface  ProductService {

//product/getcategoryname/14
    @GET("product/getcategoryname/{category_id}")
    suspend fun getCategoryname(
        @Path("category_id") categoryId: Int
    ) : String

      @GET("product/checkratingstatus/{product_id}/{user_id}")
    suspend fun checkratingStatus(
        @Path("product_id") product_id : String,
        @Path("user_id") user_id : String,

        ): Boolean

    @GET("webservice/category")
    suspend fun getCategories(): List<CategoryDto>

    @GET("webservice/district")
    suspend fun getDIstrict(): List<DistrictDto>



    @GET("product/products/{user_id}")
    suspend fun getSellerProduct(
        @Path("user_id") userId: String
    ):List<ProductDto>


    @GET("Product/productbycategory/{categoryId}/{offset}/{user_id}")
    suspend fun getProducts(
        @Path("categoryId") categoryId: Int,
        @Path("offset") offset: Int,
        @Path("user_id") user_id : String
    ): List<ProductDto>

    @GET("Product/productbyfeatured/{categoryId}/{offset}/{user_id}")
    suspend fun getFeaturedProducts(
        @Path("categoryId") categoryId: Int,
        @Path("offset") offset: Int,
        @Path("user_id") user_id : String
    ): List<ProductDto>



    @POST("Product/Product")
    @FormUrlEncoded
    suspend fun addProduct(
        @FieldMap fieldMap: Map<String, String?>
    ): CreateProductResponse

    @POST("Product/uploadproductimage")
    @Multipart
    suspend fun uploadMedia(
        @Part productId: MultipartBody.Part,
        @Part video: MultipartBody.Part?,
        @Part images: List<MultipartBody.Part>
    )


    @GET("Product/product/{productId}")
    suspend fun getsingleproduct(
        @Path("productId") productId: String
    ): ProductDto

    @GET("Product/productimage/{productId}")
    suspend fun getProductImages(@Path("productId") productId: String): List<ImageDto>

    @GET("Product/productvideo/{productId}")
    suspend fun getProductVideo(@Path("productId") productId: String): List<VideoDto>

    @FormUrlEncoded
    @POST("Product/searchproduct")
    suspend fun searchProducts(
        @Field("district") district :String,
        @Field("keyword") query: String,
        @Field("offset") offset: Int
    ): List<ProductDto>

    @POST("Webservice/postreport")
    @FormUrlEncoded
    suspend fun reportProduct(
        @Field("product_id") productId: String,
        @Field("reported_by") reportedBy: String,
        @Field("report") reportDescription: String
    )

    @GET("Product/myproduct/{userId}")
    suspend fun getMyApprovedProducts(@Path("userId") userId: String): List<ProductDto>

    @GET("Product/myproductwaiting/{userId}")
    suspend fun getMyUnApprovedProducts(@Path("userId") userId: String): List<ProductDto>

    @POST("Webservice/saveintrest")
    @FormUrlEncoded
    suspend fun showInterest(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String

    )

    @POST("Webservice/removeintrest")
    @FormUrlEncoded
    suspend fun removeInterest(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String
    )

    @POST("Webservice/checkintrest")
    @FormUrlEncoded
    suspend fun checkInterest(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String
    ): Int

    @GET("Webservice/slider")
    suspend fun sliderimageurl() : List<SLiderImageDto>



    @POST("Webservice/updateavilablestatus")
    @FormUrlEncoded
    suspend fun updateavailablestatus(
        @Field("product_available_status") productavailablestatus: String,
        @Field("product_id") productId: String
    )

    //https://www.pahadiuncle.com/api/webservice/savewishlist

    @POST("webservice/savewishlist")
    @FormUrlEncoded
    suspend fun saveWishList(
            @Field("user_id") user_id : String,
            @Field("product_id")productid : String
    ): JsonObject
    @GET("Product/productbycategory/{categoryId}/{offset}/{user_id}/1")
    suspend fun getWishlist(
            @Path("categoryId") categoryId: Int,
            @Path("offset") offset: Int,
            @Path("user_id") user_id : String
    ): List<ProductDto>

    @GET("product/setoutofstock/{product_id}")
     suspend fun setProductStock(
         @Path("product_id") productId: String
     ): Boolean


}
