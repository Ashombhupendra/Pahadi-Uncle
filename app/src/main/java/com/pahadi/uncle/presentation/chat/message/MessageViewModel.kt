package com.pahadi.uncle.presentation.chat.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.network.data.ChatDto
import com.pahadi.uncle.network.data.MessageDto
import com.pahadi.uncle.network.data.MessagesResponse
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    val sendmsgstate = MutableLiveData<NetworkState>()



    fun sendMessage(receiverId: String, message: String, product_id: String) {
        sendmsgstate.value = NetworkState.LOADING_STARTED
        viewModelScope.launch {
         val result =    ChatRepository.sendMessage(receiverId, message, product_id)
            sendmsgstate.value = NetworkState.LOADING_STOPPED
            when(result){
                is ResultWrapper.Success ->{
                    sendmsgstate.value = NetworkState.SUCCESS
                }
                is ResultWrapper.Failure ->{
                    temp_showToast("${result.errorMessage}")
                    sendmsgstate.value = NetworkState.FAILED
                }
            }
        }
    }

    fun getChatsAfter(time: String, receiverId: String, product_id : String) = liveData{
        emit(ChatRepository.getMessagesAfter(time, receiverId, product_id))
    }

    fun block(otherPersonId:String) = liveData{
        ChatRepository.blockChat(otherPersonId)
        emit(true)
    }
}