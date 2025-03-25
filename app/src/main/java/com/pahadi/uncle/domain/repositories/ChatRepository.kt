package com.pahadi.uncle.domain.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.gson.JsonSyntaxException
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.ChatService
import com.pahadi.uncle.network.data.ChatDto
import com.pahadi.uncle.network.data.MessageDto
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

object  ChatRepository {
    private const val MESSAGES_PAGE_SIZE = 4
    private val chatService = getRetrofitService(ChatService::class.java)

    suspend fun chatalert(user_id : String) = safelyCallApi {
              chatService.chatalert(user_id)
    }

    suspend fun getChatList(): List<ChatDto> {
        val userId = SharedPrefHelper.user.userId
        return try {
            val chatResponse = chatService.getChatList(userId)
            if (chatResponse.hasChat)
                chatResponse.chatList
            else emptyList()
        } catch (ex: HttpException) {
            if (ex.code() == 404)
                emptyList()
            else throw ex
        }catch (ex : UnknownHostException){
            emptyList()

        } catch (ex: java.lang.Exception) {
            emptyList()

        }catch (ex: JsonSyntaxException) {
            emptyList()
        }catch (ex : SocketTimeoutException){
            emptyList()
        }catch (ex : UnknownServiceException){
            emptyList()
        }catch (ex : IOException){
            emptyList()
        }
    }

    fun getMessages(otherPersonUserId: String, product_id: String) = Pager(
        config = PagingConfig(
            pageSize = MESSAGES_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MessagesPagingSource(product_id, otherPersonUserId, chatService)
        }
    ).liveData

    suspend fun sendMessage(receiverId: String, message: String, product_id: String) = safelyCallApi {
        val myUserId = SharedPrefHelper.user.userId
        chatService.sendMessage(myUserId, receiverId, message, product_id)
    }

    suspend fun getMessagesAfter(time: String, otherPersonId: String, product_id : String): List<MessageDto> =
        withContext(Dispatchers.IO) {
            val myUserId = SharedPrefHelper.user.userId
            try {
                val res = chatService.getMessagesAfter(time, myUserId, otherPersonId, product_id)
                Log.d("chatusermessage", res.toString())
                res.messages


            } catch (ex: Exception) {
                emptyList<MessageDto>()
            }catch (ex : UnknownHostException){
                emptyList<MessageDto>()

            } catch (ex: JsonSyntaxException) {
                emptyList<MessageDto>()
            }catch (ex : SocketTimeoutException){
                emptyList<MessageDto>()
            }catch (ex : UnknownServiceException){
                emptyList<MessageDto>()
            }catch (ex : IOException){
                emptyList<MessageDto>()
            }
        }




    suspend fun blockChat(otherPersonId: String) = safelyCallApi {
        val myUserId = SharedPrefHelper.user.userId
        chatService.chatBlock(myUserId, otherPersonId)
    }
}
