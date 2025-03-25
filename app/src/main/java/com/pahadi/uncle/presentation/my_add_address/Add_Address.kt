package com.pahadi.uncle.presentation.my_add_address

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentAddAddressBinding
import com.pahadi.uncle.databinding.FragmentLoginBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.OrderHistoryRepository
import com.pahadi.uncle.network.data.UserAddressDTO
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.my_orders.OrderViewModel
import com.pahadi.uncle.presentation.utils.showSnackBar
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch


class Add_Address : Fragment(), onAddressClick {
    private lateinit var mBinding: FragmentAddAddressBinding
    private val mOrderViewmodel by activityViewModels<OrderViewModel>()
    var screenload : Boolean = false
  private val mPaymentBool by lazy { requireArguments().getBoolean("paymentscreen") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddAddressBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = this@Add_Address
            viewmodel = mOrderViewmodel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar != null) supportActionBar.hide()
        bindProgressButton(mBinding.addSubmit)


        if (mPaymentBool){
            mBinding.addAddressToggle.rotation = 45f
            val animationSlideDown = AnimationUtils.loadAnimation(PahadiUncleApplication.instance.applicationContext,
                R.anim.slide_down)
            mBinding.addAddressContainer.startAnimation(animationSlideDown)
            mBinding.addAddressContainer.visibility =View.VISIBLE
        }else{
            mBinding.addAddressToggle.rotation = 90f
            val animationSlideUp = AnimationUtils.loadAnimation(PahadiUncleApplication.instance.applicationContext,
                R.anim.slide_up)
            mBinding.addAddressContainer.startAnimation(animationSlideUp)
            mBinding.addAddressContainer.visibility =View.GONE
        }


        mBinding.addAddressToggle.setOnClickListener {

            if (mBinding.addAddressContainer.isVisible){
                mBinding.addAddressToggle.rotation = 90f
                val animationSlideUp = AnimationUtils.loadAnimation(PahadiUncleApplication.instance.applicationContext,
                    R.anim.slide_up)
                mBinding.addAddressContainer.startAnimation(animationSlideUp)
                mBinding.addAddressContainer.visibility =View.GONE
            }else{
                mBinding.addAddressToggle.rotation = 45f
                val animationSlideDown = AnimationUtils.loadAnimation(PahadiUncleApplication.instance.applicationContext,
                    R.anim.slide_down)
                mBinding.addAddressContainer.startAnimation(animationSlideDown)
                mBinding.addAddressContainer.visibility =View.VISIBLE
            }
        }

        mOrderViewmodel.getAddress()
        mOrderViewmodel.userAddressList.observe(viewLifecycleOwner){
            val addressAdapter = AddressAdapter(it, this)
            mBinding.addlistRv.adapter = addressAdapter
            addressAdapter.notifyDataSetChanged()
        }

        mBinding.addSubmit.setOnClickListener {
            mOrderViewmodel.addAddress()
        }
        mOrderViewmodel.addAddressresponse.observe(viewLifecycleOwner){ state ->
            when(state){
                NetworkState.LOADING_STARTED ->{
                     mBinding.addSubmit.apply {
                         showProgress()
                         isCheckable = false
                     }
                }
                NetworkState.LOADING_STOPPED ->{
                    mBinding.addSubmit.apply {
                        hideProgress("Add Address")
                        isCheckable = true
                    }
                }
                NetworkState.SUCCESS ->{


                    if (mPaymentBool){
                        findNavController().navigateUp()
                    }else{
                        mBinding.addAddressToggle.rotation = 90f
                        val animationSlideUp = AnimationUtils.loadAnimation(PahadiUncleApplication.instance.applicationContext,
                            R.anim.slide_up)
                        mBinding.addAddressContainer.startAnimation(animationSlideUp)
                        mBinding.addAddressContainer.visibility =View.GONE
                        mBinding.addSubmit.apply {
                            hideProgress("Add Address")
                            isCheckable = true
                        }
                        mOrderViewmodel.getAddress()
                    }
                }
                NetworkState.FAILED ->{
                    mBinding.addSubmit.apply {
                        hideProgress("Add Address")
                        isCheckable = true
                    }

                }
            }

        }
    }

    override fun onStop() {
        super.onStop()
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.show()
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar!!.isShowing)supportActionBar.hide()
    }

    override fun onAddressDelete(addressDTO: UserAddressDTO) {
        lifecycleScope.launch {
            val result = OrderHistoryRepository.deleteAddress(addressDTO.address_id)
            when(result){
                is ResultWrapper.Success ->{
                    mOrderViewmodel.getAddress()
                    showSnackBar("Your address has been deleted")

                }
                is ResultWrapper.Failure ->{
                    temp_showToast(result.errorMessage)
                }
            }
        }
    }
}