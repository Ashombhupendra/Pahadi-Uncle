package com.pahadi.uncle.presentation.chat.chatLIst

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.domain.utils.ERROR_MSG
import com.pahadi.uncle.network.data.ChatDto
import com.pahadi.uncle.network.data.ChatResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatListViewModel : ViewModel() {
    private val _chatState = MutableLiveData<ChatListState>()
    val chatState: LiveData<ChatListState> = _chatState

    fun refreshChatList() {
        _chatState.value = Loading
        viewModelScope.launch {
            try {
                val chatList = ChatRepository.getChatList()
                Log.d("chatlist", chatList.toString())
                _chatState.value = if (chatList.isEmpty())
                    NoChatFound
                else Success(chatList)
            } catch (ex: Exception) {
                Log.d("chatlist", ex.toString())

                _chatState.value = Error(ex.localizedMessage ?: ERROR_MSG)
            }
        }
    }
}


sealed class ChatListState
object Loading : ChatListState()
data class Error(val message: String) : ChatListState()
object NoChatFound : ChatListState()
data class Success(val chatList: List<ChatDto>) : ChatListState()