package com.pahadi.uncle.presentation

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.messaging.FirebaseMessaging
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentOtpBinding
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.login.AuthType
import com.pahadi.uncle.presentation.login.AuthViewModel
import com.pahadi.uncle.presentation.login.NetworkState

class OtpFragment : Fragment() {
    private val mAuthViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mBinding: FragmentOtpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOtpBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

          val  mobile = mAuthViewModel.mobileNo.value
        bindProgressButton(mBinding.resend)
        //for hiding keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

         mBinding.resend.setOnClickListener {
             mAuthViewModel.login()
         }
         mAuthViewModel.loginNetworkState.observe(viewLifecycleOwner, Observer { state ->
              when(state){
                  NetworkState.LOADING_STARTED -> {
                      mBinding.resend.apply {
                          showProgress()
                          isEnabled = false
                      }
                  }
                  NetworkState.LOADING_STOPPED -> {
                      mBinding.resend.apply {
                          hideProgress("RESEND")
                          isEnabled = true
                      }
                  }

                  NetworkState.SUCCESS -> Log.d("success", "NetworkState.SUCCESS") //handleOtpCorrect()
                  NetworkState.FAILED -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
              }
         })
        mBinding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                mBinding.incorrectOtpMessage.visibility = View.GONE
            }

            override fun onOTPComplete(otp: String) {
                if (otp == mAuthViewModel.otp) {
                    handleOtpCorrect()
                } else {
                    mBinding.incorrectOtpMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleOtpCorrect() {
        when (mAuthViewModel.authType) {
            AuthType.EXISTING_USER -> {
                SharedPrefHelper.isLoggedIn = true

                Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT)
                    .show()
                mAuthViewModel.updateuniqid()
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()
                findNavController().navigate(R.id.homeScreenFragment, null,navOptions)
            }
            AuthType.NEW_USER -> {
                findNavController().navigate(R.id.action_otpFragment_to_registrationFragment)
            }
            AuthType.DIFFERENT_UNIQ_ID ->{
                SharedPrefHelper.isLoggedIn = true

                Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT)
                    .show()
                mAuthViewModel.updateuniqid()
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()
                findNavController().navigate(R.id.homeScreenFragment, null,navOptions)
            }

            AuthType.DIFFERENT_DEVICE -> TODO()
        }



    }

}
