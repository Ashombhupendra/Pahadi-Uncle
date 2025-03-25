package com.pahadi.uncle.presentation.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularimageview.CircularImageView
import com.pahadi.uncle.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*



class NotificationAdapter(val userList: ArrayList<notificationData>,context: Context ) :
    RecyclerView.Adapter<NotificationAdapter.MyViewholder>() {

    private val context: Context = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return MyViewholder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.bindItems(userList[position])


        holder.itemView.findViewById<RelativeLayout>(R.id.relative_notifi).setOnClickListener {
            Log.d("Notification",userList[position].name)
             val from = userList[position].notificationfrom

            val bundle = Bundle()
            bundle.putSerializable("title", userList[position].name)
            bundle.putString("message", userList[position].message)
            bundle.putString("userid", userList[position].senderid)
            bundle.putString("date", userList[position].date)
            bundle.putString("email", userList[position].email)
            bundle.putString("phone", userList[position].phone)
            bundle.putString("profile_pic", userList[position].profile_pic)
            bundle.putString("productid", userList[position].productid)
            bundle.putString("noitifcation_type", userList[position].notificationfrom)




            val activity = it.context as AppCompatActivity

              activity.findNavController(R.id.fragment).navigate(R.id.notificationView,bundle)


        }


    }
    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: notificationData) {
            val textViewName = itemView.findViewById(R.id.not_username) as TextView
            val tv_agent_usr_date  = itemView.findViewById(R.id.not_date) as TextView
            val tv_not_msg  = itemView.findViewById(R.id.not_msg) as TextView
            val iv_pro = itemView.findViewById(R.id.not_pro_pic) as CircularImageView
            val rl = itemView.findViewById<RelativeLayout>(R.id.relative_notifi)

            val message  = SpannableString(user.name)
            message.setSpan(
                ForegroundColorSpan(Color.parseColor("#003966")),
                0,
                message.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val pro = SpannableString(user.message)
            pro.setSpan(
                ForegroundColorSpan(Color.parseColor("#003966")),
                0,
                pro.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (user.notificationfrom.equals("admin")){
                iv_pro.setImageResource(R.drawable.admin_24)
               /// val finname: String = message.substring(0,1).toUpperCase(Locale.getDefault()) + message.substring(1)
                textViewName.text ="Message from Admin"


            }else{
                iv_pro.setImageResource(R.drawable.show_interest)
                val finname: String = message.substring(0,1).toUpperCase(Locale.getDefault()) + message.substring(1)
                val finmsg : String = user.message.substring(8)
                val nuifsdn :String =
                    finmsg?.replace("Product ", "",false)?.replace("Name ","",false).toString()

                textViewName.text =finname
                textViewName.append(", "+nuifsdn+" ")
              //  textViewName.append(pro)


            }

            tv_agent_usr_date.text = user.date
            tv_not_msg.text = user.message
            textViewName.maxLines =2
            textViewName.ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

    /////********************for converting time into millisecond ***********************////
    @SuppressLint("SimpleDateFormat")
    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy H:m:ss")
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println("Today is $date")
        return date.time
    }

}