package com.pahadi.uncle.presentation.chat.chatLIst

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ChatDto
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.launch

class ChatListFragment : Fragment(R.layout.fragment_chat_list), OnChatClickedListener {
    private val chatViewModel by activityViewModels<ChatListViewModel>()
    private lateinit var chatListRV: RecyclerView
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var noChatFoundMessage: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatListRV = view.findViewById(R.id.chat_list_rv)
        progressIndicator = view.findViewById(R.id.progress_indicator)
        noChatFoundMessage = view.findViewById(R.id.no_chats_message)

        chatViewModel.refreshChatList()


        chatViewModel.chatState.observe(viewLifecycleOwner) { chatListState ->


            when (chatListState) {
                is Success -> {
                    progressIndicator.visibility = View.GONE
                    noChatFoundMessage.visibility = View.GONE
                    chatListRV.adapter = ChatListAdapter(chatListState.chatList, this)
                }
                is Error -> {
                    progressIndicator.visibility = View.GONE

                    showSnackBar(chatListState.message)
                }
                is Loading ->{
                    progressIndicator.visibility = View.VISIBLE
                }
                is  NoChatFound ->{
                    progressIndicator.visibility = View.GONE

                    noChatFoundMessage.visibility = View.VISIBLE
                }

            }
        }
    }

    override fun onChatClicked(chatDto: ChatDto) {
        val myUserId = SharedPrefHelper.user.userId
        val otherPersonUserId =
            if (myUserId == chatDto.senderId) chatDto.receiverId else chatDto.senderId
        val args = bundleOf(
            "other_person_user_id" to otherPersonUserId,
            "other_person_name" to chatDto.nameFormatted,
            "other_person_profile_image" to chatDto.imageUrl,
            "product_id" to chatDto.product_id

        )
        findNavController().navigate(R.id.sendMessageFragment, args)
    }
}

interface OnChatClickedListener {
    fun onChatClicked(chatDto: ChatDto)
}