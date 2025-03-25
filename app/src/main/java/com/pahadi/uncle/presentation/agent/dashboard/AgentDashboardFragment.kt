package com.pahadi.uncle.presentation.agent.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentAgentDashboardBinding
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.utils.showSnackBar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class AgentDashboardFragment : Fragment(R.layout.fragment_agent_dashboard) {
    private val viewModel: AgentDashboardViewModel by viewModels()
    private lateinit var binding: FragmentAgentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentDashboardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val agent = (SharedPrefHelper.agentLoginStatus as AgentLoginStatus.LoggedIn).agentDto
        binding.agentCode = agent.agentCode
        binding.agentaName = agent.agentName
         Log.d("agentdetail", agent.toString())

        binding.agentNotification.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("notify_type", "agent")

            findNavController().navigate(R.id.notificationFragment, bundle)
        }
        // **************No Internet Dialog: Signal**************//
        NoInternetDialogSignal.Builder(
            requireContext() as Activity,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }

                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
        // **************No Internet Dialog: Signal END**************//



        binding.user.setOnClickListener {
          //  Toast.makeText(requireContext(), "User List", Toast.LENGTH_SHORT).show()
           findNavController().navigate(R.id.userListFragment)
        }
        //for hiding keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)


        viewModel.getCounts(agent.agentCode).observe(viewLifecycleOwner) { agentCounts ->
            binding.progressIndicator.visibility = View.GONE
            binding.agentCounts = agentCounts
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.agent_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // put your code here
                customalertdilaoglogout()
                return true
            }
            R.id.changePassword ->{
                findNavController().navigate(R.id.agentPasswordChange)
                return true
                
            }
        }
        return false
    }


    private fun customalertdilaoglogout(){
      SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("LOGOUT")
            .setContentText("Are you sure you want to Logout...")
            .setCancelText("No")
            .setConfirmText("Logout")

            .setConfirmButton("LOGOUT", SweetAlertDialog.OnSweetClickListener {
               // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)
                it.showCancelButton(false)

                SharedPrefHelper.agentLoginStatus = AgentLoginStatus.NotLoggedIn
                it.setTitleText("LOGOUT").setContentText("You are successfully logout ").setConfirmText("OK")
                    .setConfirmClickListener( SweetAlertDialog.OnSweetClickListener {
                        it.cancel()
                        it.setCanceledOnTouchOutside(false)
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()

                        findNavController().navigate(R.id.homeScreenFragment, null,navOptions)


                    } ).changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            })
          .apply { setCanceledOnTouchOutside(false) }

          .show()


    }
}

