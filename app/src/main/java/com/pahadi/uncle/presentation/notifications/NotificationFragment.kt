package com.pahadi.uncle.presentation.notifications

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AgentRepository
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.NotificationDTO
import com.pahadi.uncle.network.data.SLiderImageDto
import com.pahadi.uncle.presentation.MainActivity
import com.pahadi.uncle.presentation.agent.dashboard.AgentCustomAdapter
import com.pahadi.uncle.presentation.agent.dashboard.Agentusers
import com.pahadi.uncle.presentation.home.Slider.SliderItem
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class NotificationFragment : Fragment() {
    val sliderlist1 = MutableLiveData<List<notificationData>>()
    private lateinit var progressIndicator: LinearProgressIndicator
      var alist = ArrayList<notificationData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? {

        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SharedPrefHelper.setNotificationAlert("")
        val recyclerView = view.findViewById(R.id.rv_notification) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.startLayoutAnimation()
        progressIndicator = view.findViewById(R.id.notification_progress_indicator)
        progressIndicator.isVisible = view.isVisible
        val  users = ArrayList<notificationData>()
        val bundle = arguments
        if (bundle != null){
            val type = bundle.getString("notify_type")
            Log.d("notificationtype", type.toString())
            if (type.equals("agent")){

                lifecycleScope.launch {
                    val agent = (SharedPrefHelper.agentLoginStatus as AgentLoginStatus.LoggedIn).agentDto

                    val result = AgentRepository.getAgentNOtification(agent.id)
                    when(result){
                        is ResultWrapper.Success ->{



                            val sliderList = mutableListOf<notificationData>()
                            sliderList.addAll(result.response.map { tonotificationitem(it) })
                            Log.d("Notification",result.response.toString())
                            sliderlist1.value = sliderList
                            progressIndicator.isVisible = view.isVisible.not()

                        }
                        is ResultWrapper.Failure -> {
                            Log.d("Notification f ", result.errorMessage)
                            progressIndicator.isVisible = view.isVisible.not()
                            showSnackBar("No Notification were found.")
                            val nofound = view.findViewById<TextView>(R.id.notification_no_found) as TextView
                            nofound.text = "No Notification were found."
                            nofound.visibility = View.VISIBLE
                        }
                    }
                }
            }else{
                lifecycleScope.launch {
                    val userid = SharedPrefHelper.user.userId.toString()

                    val result = AuthRepository.getNotification(userid)
                    when(result){
                        is ResultWrapper.Success ->{



                            val sliderList = mutableListOf<notificationData>()
                            sliderList.addAll(result.response.map { tonotificationitem(it) })
                            Log.d("Notification",result.response.toString())
                            sliderlist1.value = sliderList
                            progressIndicator.isVisible = view.isVisible.not()

                        }
                        is ResultWrapper.Failure -> {
                            Log.d("Notification f ", result.errorMessage)
                            progressIndicator.isVisible = view.isVisible.not()
                            showSnackBar("No Notification were found.")
                            val nofound = view.findViewById<TextView>(R.id.notification_no_found) as TextView
                            nofound.text = "No Notification were found."
                            nofound.visibility = View.VISIBLE
                        }
                    }
                }

        }


        }

       sliderlist1.observe(requireActivity(), Observer {

      //   val list =   Collections.reverse(it)
           val adapter = NotificationAdapter(it  as ArrayList<notificationData>,requireContext())
           recyclerView.adapter = adapter
           adapter.notifyDataSetChanged()
       })



    }

    private fun tonotificationitem(sLiderImageDto: NotificationDTO) = notificationData(
        sLiderImageDto.username+"",sLiderImageDto.sender_id+"",sLiderImageDto.message+"",sLiderImageDto.created+"",sLiderImageDto.notification_type+"",
        sLiderImageDto.email+"",sLiderImageDto.Phone+"", sLiderImageDto.productid+"",sLiderImageDto.profile_pic +""
    )
}