package com.pahadi.uncle.presentation.agent.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.razir.progressbutton.bindProgressButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.utils.API_KEY
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class UserListFragment : Fragment() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val client = OkHttpClient()
    private lateinit var progressIndicator: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById(R.id.userlist_rv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.startLayoutAnimation()
        val agent = (SharedPrefHelper.agentLoginStatus as AgentLoginStatus.LoggedIn).agentDto
        progressIndicator = view.findViewById(R.id.user_agent_progress_indicator)
        progressIndicator.isVisible = view.isVisible
        val  users = ArrayList<Agentusers>()
        AndroidNetworking.initialize(requireContext());
        AndroidNetworking.get("http://pahadiuncle.com/api/Webservice/agentusers/"+agent.agentCode)

            .addHeaders("x-api-key", API_KEY)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    progressIndicator.isVisible = view.isVisible.not()

                    val jsonarray : JSONArray? = response?.getJSONArray("users")
                         val status = response!!.getString("status")
                    if (status.equals("true")){
                    for (i in 0..jsonarray!!.length()-1){
                        val JO: JSONObject = jsonarray.getJSONObject(i)
                        val name : String = JO.getString("username")
                        val finname: String = name.substring(0,1).toUpperCase(Locale.getDefault()) + name.substring(1)
                        val date : String = JO.getString("created")
                        val email : String = JO.getString("email")
                        val phnnumber : String = JO.getString("phone")
                        val product_count : String = JO.getString("product_count")

                        users.add(Agentusers(finname, product_count,date,email,phnnumber))

                    }
                    val adapter = AgentCustomAdapter(users,PahadiUncleApplication.instance.applicationContext)
                    recyclerView.adapter = adapter
                    }
                    else{
                         val nofound = view.findViewById<TextView>(R.id.agent_user_no_found) as TextView
                        nofound.text = "No User Found !"
                        progressIndicator.isVisible = view.isVisible.not()
                        nofound.visibility = view.visibility
                    }

                }

                override fun onError(anError: ANError?) {
                    progressIndicator.isVisible = view.isVisible.not()

                    Log.d("RESULTAGENT ERROR", anError?.message.toString())
                }

            })




  }

}