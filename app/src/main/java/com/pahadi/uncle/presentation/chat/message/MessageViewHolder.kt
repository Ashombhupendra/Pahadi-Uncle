package com.pahadi.uncle.presentation.chat.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pahadi.uncle.R
import com.pahadi.uncle.network.data.ChatDto
import com.pahadi.uncle.network.data.MessageDto
import com.pahadi.uncle.presentation.utils.loadProfileImage

class MessageViewHolder(view: View, private val isMyMessage: Boolean) : RecyclerView.ViewHolder(view) {
    private val message: TextView = view.findViewById(R.id.message)
    private val msg_date_time: TextView = view.findViewById(R.id.message_date_time)
    private val profileImage: ImageView = view.findViewById(R.id.profile_image)
    private val message_status: ImageView = view.findViewById(R.id.message_status)
    fun bind(messageDto: MessageDto, otherPersonProfileImage: String?) {
        message.text = messageDto.message
        msg_date_time.text = messageDto.created

        if (messageDto.read_status.equals("1")){
            message_status.setImageResource(R.drawable.seen_message)
        }else{
            message_status.setImageResource(R.drawable.delivered_message)
        }
        if (isMyMessage) {
            loadProfileImage(profileImage, messageDto.profile_img)
        } else {
            loadProfileImage(profileImage, messageDto.profile_img)
        }
    }

    companion object {
        fun create(parent: ViewGroup, layoutId: Int): MessageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(layoutId, parent, false)
            return MessageViewHolder(view, layoutId == R.layout.item_my_message)
        }
    }
}
