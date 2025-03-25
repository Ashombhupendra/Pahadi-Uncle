package com.pahadi.uncle.presentation.signup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentSellerLocationBinding
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.login.AuthViewModel
import com.pahadi.uncle.presentation.login.NetworkState
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.messaging.FirebaseMessaging
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.presentation.MainActivity
import com.pahadi.uncle.presentation.utils.hideKeyboard
import kotlinx.coroutines.launch

class SellerLocationFragment : Fragment() {
    private lateinit var mBinding: FragmentSellerLocationBinding
    private val mAuthViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSellerLocationBinding.inflate(layoutInflater, container, false)
        mBinding.authVM = mAuthViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.pincode.apply {
            addTextChangedListener {

                lifecycleScope.launch {
                    val result = AuthRepository.getPostOffice(it.toString())
                    mAuthViewModel.pinCode.value = it.toString()
                    if (!result?.city.equals(null)){

                        mAuthViewModel.district.value= result?.district
                        mAuthViewModel.state.value = result?.state

                        mBinding.state.text = result?.state?.toEditable()
                        mBinding.district.text = result?.district?.toEditable()
                        mBinding.proCity.setOnTouchListener(object : View.OnTouchListener{
                            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                                hideKeyboard(requireContext(), mBinding.proCity)
                                return false
                            }

                        })
                        val conditionadapter =object : ArrayAdapter<String>(
                            requireContext(),
                            R.layout.item_spinner,
                            result!!.gpoList

                        ){
                            override fun getDropDownView(
                                position: Int,
                                convertView: View?,
                                parent: ViewGroup
                            ): View {
                                val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                                // set item text size

                                if (position.toLong() == mBinding.proCity.selectedItemPosition.toLong() && position != 0 ){
                                    tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                                    tv.setTextColor(Color.parseColor("#ffffff"))

                                }else{
                                    tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                                    tv.setTextColor(Color.parseColor("#000000"))

                                }



                                return tv
                            }
                        }
                        mBinding.proCity.adapter = conditionadapter
                        mBinding.proCity.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val conItem = conditionadapter.getItem(position)
                                val indexs = result?.gpoList?.indexOf(conItem)
                                if (indexs !=0){
                                    //  Toast.makeText(requireContext(), conItem  + indexs,Toast.LENGTH_SHORT).show()
                                    mAuthViewModel.city.value = conItem
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }


                        }

                    }
                }

            }
        }

        mAuthViewModel.registerNetworkState.observe(viewLifecycleOwner) { state ->
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
//                    val intent = requireActivity().intent
//                    intent.putExtra("logout","log")
////                intent.addFlags(
////                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
////                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
////                )
//                    requireActivity().overridePendingTransition(0, 0)
//                    requireActivity().finish()
//
//                    requireActivity().overridePendingTransition(0, 0)
//                    startActivity(intent)
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
        }
    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
