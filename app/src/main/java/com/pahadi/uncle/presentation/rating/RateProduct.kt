package com.pahadi.uncle.presentation.rating

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentRateProductBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.RatingRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.utils.hideKeyboard
import com.pahadi.uncle.presentation.utils.showSnackBar
import com.pahadi.uncle.presentation.utils.temp_showToast
import kotlinx.coroutines.launch


class RateProduct : DialogFragment() {
    private var mView: View? = null
    private lateinit var mBinding: FragmentRateProductBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBinding = FragmentRateProductBinding.inflate(layoutInflater, null, false)
        mView = mBinding.root
        mBinding.frag = this

        return MaterialAlertDialogBuilder(requireContext()).setView(mBinding.root).create()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.ratingDialogCancel.setOnClickListener {
            hideKeyboard(requireContext(), it)
            dismiss()
        }

        bindProgressButton(mBinding.ratingSubmit)
        mBinding.rateProductBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (rating != 0f){
                mBinding.noRate.visibility = View.GONE
            }
        }
        mBinding.rateProductEt.setOnClickListener {
            mBinding.rateProductEt.setError(null)
        }

    }

    override fun onDestroy() {
        mView = null
        super.onDestroy()
    }

    fun submitrate(){
        hideKeyboard(requireContext(), mBinding.ratingSubmit)

        val rating = mBinding.rateProductBar.rating
           val review = mBinding.rateProductEt.text
        if (rating == 0f){
            mBinding.noRate.visibility = View.VISIBLE


        }else if (review.isNullOrEmpty() ){
            mBinding.rateProductEt.setError("Enter your feedback....")

        }else{
            val bundle = arguments
            val productId = bundle?.getString("product_id")
            mBinding.ratingSubmit.apply {
                showProgress()
                isEnabled = false
            }
            val userid = SharedPrefHelper.user.userId
            lifecycleScope.launch {
                val result = RatingRepository.postRating(userid.toString(),
                    productId!!, rating.toInt(), review.toString())
                when(result){
                    is ResultWrapper.Success ->{
                        Log.d("ratingresult", result.response.toString())
                        mBinding.ratingSubmit.apply {
                            hideProgress("Submit")
                            isEnabled = true
                        }

                        dismiss()
                        productActiveDeactiveDialog("Pahadi Uncle \n Thankyou! For your valuable feedback")
                        findNavController().navigate(R.id.homeScreenFragment)
                     //   temp_showToast("${result.response}")
                    }
                    is ResultWrapper.Failure ->{
                        temp_showToast("${result.errorMessage}")
                        mBinding.ratingSubmit.apply {
                            hideProgress("Submit")
                            isEnabled = true
                        }
                    }
                }
            }
        }

    }
    fun productActiveDeactiveDialog(message: String){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_review_success_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext(), R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
      //  mAlertDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)
            //  text.setText(message)
        btn.setOnClickListener {

            mAlertDialog.cancel()

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.PauseDialogAnimation)

    }
}