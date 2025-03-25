package com.pahadi.uncle.presentation.login

 import android.app.Activity
 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
 import com.google.firebase.messaging.FirebaseMessaging
 import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentLoginBinding
 import com.pahadi.uncle.domain.utils.SharedPrefHelper
 import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
 import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class LoginFragment : Fragment() {
    private val mAuthViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = this@LoginFragment
            viewModel = mAuthViewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindProgressButton(mBinding.submit)

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

        mAuthViewModel.loginNetworkState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                NetworkState.LOADING_STARTED -> {
                    mBinding.submit.apply {
                        showProgress()
                        isEnabled = false
                    }
                }
                NetworkState.LOADING_STOPPED -> {
                    mBinding.submit.apply {
                        hideProgress("Submit")
                        isEnabled = true
                    }
                }
                NetworkState.SUCCESS -> {
                    if (mAuthViewModel.authType == AuthType.DIFFERENT_DEVICE) {
                        showDiffDevicePopup()
                    }

                    else {
                        mAuthViewModel.loginNetworkState.value = null

                        //for fragment finish

                        findNavController().navigate(R.id.action_loginFragment_to_otpFragment)
                      }
                }
                NetworkState.FAILED -> {
                    Snackbar.make(view, mAuthViewModel.errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        })


        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val currentToken = it.result
                currentToken?.let { token ->

                    SharedPrefHelper.stoken = token
                    Log.d("token", token)
                    mAuthViewModel.stoken.value = token


                }
            }
        }
    }

    private fun showDiffDevicePopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Multiple Devices Detected")
            .setMessage(R.string.multiple_login_message)
            .show()
    }
}
