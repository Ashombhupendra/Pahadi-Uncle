package com.pahadi.uncle.presentation.agent.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentAgentLoginBinding
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.utils.showSnackBar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class AgentLoginFragment : Fragment() {
    private lateinit var binding: FragmentAgentLoginBinding
    private val viewModel: AgentLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val agentLoginStatus = SharedPrefHelper.agentLoginStatus

        //going to dashboard fragment directly is agent is already logged in.
        if (agentLoginStatus is AgentLoginStatus.LoggedIn) {
            findNavController().navigate(R.id.action_agentLoginFragment_to_agentDashboardFragment)
        }

        viewModel.loginState.observe(viewLifecycleOwner, Observer { loginState ->
            binding.progressIndicator.isVisible = loginState is LoginState.Loading
            when (loginState) {
                is LoginState.Success -> {
                    findNavController().navigate(R.id.action_agentLoginFragment_to_agentDashboardFragment)

                }
                is LoginState.Failed -> {
                    showSnackBar(loginState.reason)
                }

                LoginState.Loading -> TODO()
            }
        })
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val currentToken = it.result
                currentToken?.let { token ->

                    SharedPrefHelper.stoken = token
                    Log.d("token", token)
                   viewModel.token.value = token


                }
            }
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

    }
}
