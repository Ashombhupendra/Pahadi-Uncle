package com.pahadi.uncle.presentation.chat.message

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.data.MessageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SendMessageFragment : Fragment(R.layout.fragment_send_message) {
    private val viewModel: MessageViewModel by viewModels()
    private var lastMessagesLoadedAt: String = ""
    private lateinit var messagesPagingDataAdapter: MessagesPagingDataAdapter
    private lateinit var messagesRv: RecyclerView
    lateinit var progresslayout : LinearProgressIndicator

    private var scrollboolean :Boolean = false

    private var otherPersonId = ""
    private var productID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    companion object{
        val lastmessage = MutableLiveData<String>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesRv = view.findViewById(R.id.message_rv)
        val messageET: EditText = view.findViewById(R.id.message_et)
        val sendButton: ImageButton = view.findViewById(R.id.send_button)
        progresslayout= view.findViewById(R.id.message_progress_indicator)

        otherPersonId = requireArguments().getString("other_person_user_id")!!
        productID = requireArguments().getString("product_id")!!

        val profileImageUrl = requireArguments().getString("other_person_profile_image")

        messagesPagingDataAdapter = MessagesPagingDataAdapter(profileImageUrl)
        messagesRv.adapter = messagesPagingDataAdapter

        ChatRepository.getMessages(otherPersonId, productID).observe(viewLifecycleOwner) {
            messagesPagingDataAdapter.submitData(lifecycle, it)

        }

        lastmessage.value = convertLongToDatetime(System.currentTimeMillis())

        sendButton.setOnClickListener {
            val message = messageET.text.toString()
            messageET.setText("")
             progresslayout.visibility = View.VISIBLE
            scrollboolean = true

            if (message.isBlank()) return@setOnClickListener
            viewModel.sendMessage(otherPersonId, message, productID)
           /* val newMessage = MessageDto(
                created = convertLongToDatetime(System.currentTimeMillis()),
                id = System.currentTimeMillis().toInt(),
                message = message,
                receiverId = otherPersonId,
                senderId = SharedPrefHelper.user.userId
            )*/
         //   addMessages(listOf(newMessage))
        }


        log(lastMessagesLoadedAt)


        startPolling(otherPersonId)
    }

    private fun startPolling(receiverId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            repeat(Int.MAX_VALUE) {
                delay(5000)

                if (  !lastmessage.value.isNullOrEmpty() && PahadiUncleApplication.instance.isConnectedToInternet()){


                    Log.d("chattime",  formatDateFromDateString("yyyy-MM-dd HH:mm:ss", "HH:mm:ss", lastmessage.value.toString()).toString())
                viewModel.getChatsAfter(formatDateFromDateString("yyyy-MM-dd HH:mm:ss",
                    "HH:mm:ss", lastmessage.value.toString()).toString(), receiverId,productID)
                    .observe(viewLifecycleOwner) {
                        addMessages(it)


                   //     lastMessagesLoadedAt = convertLongToTime(System.currentTimeMillis())

                    }


            }
            }

        }
    }

    private fun addMessages(messages: List<MessageDto>) {
        //Remove duplicate here.
        val updatedList: MutableList<MessageDto> =
            messagesPagingDataAdapter.snapshot().toMutableList() as MutableList<MessageDto>
        updatedList.addAll(0, messages)

        messagesPagingDataAdapter.submitData(lifecycle, PagingData.from(updatedList.distinctBy { it.id }))
        progresslayout.visibility = View.GONE
        lastmessage.value = convertLongToDatetime(System.currentTimeMillis())
        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            if (scrollboolean){
                messagesRv.scrollToPosition(0)

            }
        }
    }

    private fun getFormattedString(timeInMillies: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date = Date(timeInMillies)
        return dateFormat.format(date)
    }


    fun convertLongToDatetime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }


    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm:ss")
        return format.format(date)
    }

    @Throws(ParseException::class)
    fun formatDateFromDateString(
        inputDateFormat: String?, outputDateFormat: String?,
        inputDate: String?
    ): String? {
        val mParsedDate: Date
        val mOutputDateString: String
        val mInputDateFormat = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat(outputDateFormat, Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        mOutputDateString = mOutputDateFormat.format(mParsedDate)
        return mOutputDateString
    }



    private fun getCurrentTimeFormatted(): String {
        val currentTime = System.currentTimeMillis() - 19800000 //converting to utc time.
        return getFormattedString(currentTime)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.block_chat) {
            viewModel.block(otherPersonId).observe(viewLifecycleOwner){
                findNavController().navigateUp()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}