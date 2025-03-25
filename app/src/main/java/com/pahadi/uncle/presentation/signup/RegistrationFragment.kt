package com.pahadi.uncle.presentation.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentRegistrationBinding
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.login.AuthViewModel
import com.pahadi.uncle.presentation.login.NetworkState

class RegistrationFragment : Fragment() {
    private val mAuthViewModel by activityViewModels<AuthViewModel>()
    private lateinit var mBinding: FragmentRegistrationBinding
    var enableagentcode : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRegistrationBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = this@RegistrationFragment
            viewModel = mAuthViewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            mBinding.agentCode.doAfterTextChanged {
                 if (it!!.length > 1){
                     mAuthViewModel.getAgentCodeVAlidation()
                 }else{
                     mAuthViewModel.erroragentcode.value = null
                     mBinding.agentcodeError.setText(null)


                 }
            }


        mAuthViewModel.buyerregisterNetworkState.observe(viewLifecycleOwner) { state ->
            when (state) {
                NetworkState.LOADING_STARTED -> {
                    mBinding.submit.apply {
                        showProgress()
                        isEnabled = false
                    }
                }
                NetworkState.LOADING_STOPPED -> {
                    mBinding.submit.apply {
                        hideProgress("SUBMIT")
                        isEnabled = true
                    }
                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(
                        requireContext(),
                        "User Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    SharedPrefHelper.isLoggedIn = true
                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()

                    findNavController().navigate(R.id.homeScreenFragment, null, navOptions)
                }
                NetworkState.FAILED -> {
                    Snackbar.make(view, mAuthViewModel.errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val currentToken = it.result
                currentToken?.let { token ->

                    SharedPrefHelper.stoken = token
                    Log.d("token", token)
                    mAuthViewModel.stoken.value = token


                }
            }


            mBinding.submit.setOnClickListener {

                Log.d("submitres" ,"${mAuthViewModel.agentcodevalid.value}, ${mAuthViewModel.agentCode.value.isNullOrEmpty()} ,${mAuthViewModel.agentCode.value.toString()}")
                if (mAuthViewModel.agentcodevalid.value!!.equals(true) || mAuthViewModel.agentCode.value.isNullOrEmpty()){
                    mAuthViewModel.registerBUyerProfile()
                }else{

                }



            }
        }
}
}
