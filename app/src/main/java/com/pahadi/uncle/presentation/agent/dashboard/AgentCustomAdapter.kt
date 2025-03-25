package com.pahadi.uncle.presentation.agent.dashboard

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R


class AgentCustomAdapter(val userList: ArrayList<Agentusers>, context : Context) : RecyclerView.Adapter<AgentCustomAdapter.ViewHolder>() {
    private val context: Context = context

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentCustomAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.agen_user_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AgentCustomAdapter.ViewHolder, position: Int) {
        holder.bindItems(userList[position])
//        holder.itemView.findViewById<LinearLayout>(R.id.ll_agent_user).setOnClickListener {
//         val posss = userList.get(position)
//            Log.d("CLICK", "CLICK")
//            Toast.makeText(context, "UserDetail \n "+ "Username : "+posss.name+"\nEmail : "+posss.email+
//                               "\nPhone Number : "+posss.phn + "\n Created Date : "+ posss.date, Toast.LENGTH_LONG).show()
//        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: Agentusers) {
            val textViewName = itemView.findViewById(R.id.textViewUsername) as TextView
            val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
            val tv_agent_usr_date  = itemView.findViewById(R.id.tv_agent_usr_date) as TextView

            textViewName.text = user.name
            textViewAddress.text = user.product
            tv_agent_usr_date.text = user.date
        }
    }
}