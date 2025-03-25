package com.pahadi.uncle.domain.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.google.gson.JsonSyntaxException
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.ChatService
import com.pahadi.uncle.network.data.MessageDto
import com.pahadi.uncle.network.data.ProductDto
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

class MessagesPagingSource(
    private val product_id : String,
    private val otherPersonUserId: String,
    private val chatService: ChatService
) : PagingSource<Int, MessageDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDto> {
        val position = params.key ?: 0
        return try {
            val myUserId = SharedPrefHelper.user.userId
            val messagesResponse = chatService.getMessages(myUserId, otherPersonUserId,position,product_id)
            Log.d("chatmessageuser", messagesResponse.toString())

            LoadResult.Page(
                data = messagesResponse.messages,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (messagesResponse.messages.isEmpty()) null else position + 1
            )
        } catch (ex: HttpException) {
            if (ex.code() == 404)
                LoadResult.Page(
                    data = emptyList(),
                    nextKey = null,
                    prevKey = position - 1
                )
            else LoadResult.Error(ex)
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }catch (ex : UnknownHostException){
            LoadResult.Error(ex)

        } catch (ex: JsonSyntaxException) {
            LoadResult.Error(ex)
        }catch (ex : SocketTimeoutException){
            LoadResult.Error(ex)
        }catch (ex : UnknownServiceException){
            LoadResult.Error(ex)
        }catch (ex : IOException){
            LoadResult.Error(ex)
        }
    }
}
