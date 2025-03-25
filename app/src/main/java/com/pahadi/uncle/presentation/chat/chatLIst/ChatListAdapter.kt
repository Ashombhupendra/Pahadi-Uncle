package com.pahadi.uncle.presentation.chat.chatLIst

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.databinding.ItemChatListBinding
import com.pahadi.uncle.network.data.ChatDto

class ChatListAdapter(private val chatList: List<ChatDto>, private val onChatClickedListener: OnChatClickedListener) :
    RecyclerView.Adapter<ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChatViewHolder.create(parent)

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatDto = chatList[position]
        holder.binding.chat = chatDto
        holder.binding.root.setOnClickListener {
            onChatClickedListener.onChatClicked(chatDto)
        }
    }

    override fun getItemCount() = chatList.size
}


class ChatViewHolder(val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): ChatViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChatListBinding.inflate(layoutInflater, parent, false)
            return ChatViewHolder(binding)
        }
    }
}