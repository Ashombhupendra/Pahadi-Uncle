package com.pahadi.uncle.presentation.chat.message

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.MessageDto

class MessagesPagingDataAdapter(private val otherPersonProfileImage: String?) :
    PagingDataAdapter<MessageDto, MessageViewHolder>(itemCallback) {

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<MessageDto>() {
            override fun areItemsTheSame(oldItem: MessageDto, newItem: MessageDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MessageDto, newItem: MessageDto): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val chatDto = getItem(position)
        chatDto ?: return
        holder.bind(chatDto, otherPersonProfileImage)
        Log.d("chatitems", chatDto.toString())




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.create(parent = parent, layoutId = viewType)
    }

    override fun getItemViewType(position: Int): Int {
        val messageDto = getItem(position)
        val myUserId = SharedPrefHelper.user.userId
        val myMessage = messageDto?.senderId == myUserId
        return if (myMessage) R.layout.item_my_message else R.layout.item_other_person_message
    }

}
