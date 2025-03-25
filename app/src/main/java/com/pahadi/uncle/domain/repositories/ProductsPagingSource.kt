package com.pahadi.uncle.domain.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.google.gson.JsonSyntaxException
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.ProductService
import com.pahadi.uncle.network.data.ProductDto
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

class ProductsPagingSource(
    private val service: ProductService,
    private val categoryId: Int,
    private val userid : String
) : PagingSource<Int, ProductDto>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDto> {
            val position = params.key ?: 0



        return try {

            val products = service.getProducts(categoryId, position, userid)

            Log.d("PRODUCTLIST1", products.toString())
            LoadResult.Page(
                data = products,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (products.isEmpty()) null else position + 1
            )
        } catch (ex: JsonSyntaxException) {
            log(ex.localizedMessage)

            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            if (ex.code() == 404) {

                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (position == 0) null else position - 1,
                    nextKey = null
                )
            } else {
                LoadResult.Error(ex)
            }
        }catch (ex : UnknownHostException){
            Log.d("ERROR1", ex.localizedMessage)
            log(ex.localizedMessage)
            LoadResult.Error(ex)
        } catch (ex: Exception) {
            Log.d("ERROR1", ex.localizedMessage)
            log(ex.localizedMessage)
            LoadResult.Error(ex)
        }catch (ex : SocketTimeoutException){
            Log.d("ERROR1", ex.localizedMessage)
            log(ex.localizedMessage)
            LoadResult.Error(ex)
        }catch (ex : UnknownServiceException){
            Log.d("ERROR1", ex.localizedMessage)
            log(ex.localizedMessage)
            LoadResult.Error(ex)
        }catch (ex : IOException){
            Log.d("ERROR1", ex.localizedMessage)
            log(ex.localizedMessage)
            LoadResult.Error(ex)
        }

    }
}