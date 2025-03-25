package com.pahadi.uncle.presentation.my_orders.paymentscreen

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentMyOrdersBinding
import com.pahadi.uncle.databinding.FragmentPaymentScreenBinding
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.my_orders.OrderViewModel
import com.pahadi.uncle.presentation.utils.hideKeyboard
//import com.razorpay.Checkout
//import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.lang.Exception


class PaymentScreen : Fragment()/*, PaymentResultListener*/ {
    private val mOrderViewmodel by activityViewModels<OrderViewModel>()
    private lateinit var mBinding: FragmentPaymentScreenBinding
   val list = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPaymentScreenBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mOrderViewmodel.getAddress()
//        Checkout.preload(PahadiUncleApplication.instance.applicationContext)
        mOrderViewmodel.userAddressList.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                list.clear()

                it.forEach {

                    list.add("${it.flat}, ${it.area}, ${it.landmark}, ${it.city}, ${it.state}, India ${it.pincode}")
                }
                list.add("Add new address")
                AddressSpinner(list)
            }
        }

        mOrderViewmodel.myPlaceOrderPrice.observe(viewLifecycleOwner){
            mBinding.finalPaymentPrice.text = "₹ $it.00 /-"
        }
        mBinding.submitPayment.setOnClickListener {
            if (mBinding.paymentCodBtn.isChecked){
                val gson = Gson()
                val liststring = gson.toJson(mOrderViewmodel.selectProductlist)
                Log.d("itemprod", liststring)
             // Toast.makeText(requireContext(),"COD Apply $liststring", Toast.LENGTH_SHORT).show()
                mOrderViewmodel.paymentmode.value = "COD"

                mOrderViewmodel.doTransaction(liststring).let {

                    findNavController().navigate(R.id.action_paymentScreen_to_my_Orders)

                }

            }else{
                Toast.makeText(requireContext(),"Payment gateway is not available, please switch to COD", Toast.LENGTH_SHORT).show()

//                 startPayment()

            }

        }


    }


    private fun AddressSpinner( list : List<String>){
        ///****************For condition Spinner ******************///////////
        mBinding.selectAddress.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard(requireContext(), mBinding.selectAddress)
                return false
            }

        })
        val conditionadapter =object : ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            list

        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                // set item text size

                if (position.toLong() == mBinding.selectAddress.selectedItemPosition.toLong()  ){
                    tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                    tv.setTextColor(Color.parseColor("#ffffff"))

                }else{
                    tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                    tv.setTextColor(Color.parseColor("#000000"))

                }



                return tv
            }
        }
        mBinding.selectAddress.adapter = conditionadapter

        mBinding.selectAddress.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val conItem = conditionadapter.getItem(position)
                val indexs = list.indexOf(conItem)

                if (conItem.equals("Add new address")){
                    val args = bundleOf("paymentscreen" to true)
                     findNavController().navigate(R.id.add_Address,args)
                }else{
                    mOrderViewmodel.selectedaddress.value = conItem
                    //   Toast.makeText(requireContext(), conItem  + indexs,Toast.LENGTH_SHORT).show()
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }


        }

    }

//    fun startPayment() {
//        /*
//          You need to pass current activity in order to let Razorpay create CheckoutActivity
//         */
//        val payamount = mOrderViewmodel.myPlaceOrderPrice.value.toString()
//        val activity: Activity = requireActivity()
//        val co = Checkout()
//
//        try {
//            val options = JSONObject()
//            val user = SharedPrefHelper.user
//            options.put("name", "${user.userName}")
//
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
//            options.put("currency", "INR")
//            options.put("amount", payamount+"00")
//
//            co.open(activity, options)
//        } catch (e: Exception) {
//            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
//                .show()
//            e.printStackTrace()
//        }
//    }

//    override fun onPaymentSuccess(razorpayPaymentID: String?) {
//
//        try {
//            Toast.makeText(requireContext(), "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
//            val gson = Gson()
//            val liststring = gson.toJson(mOrderViewmodel.selectProductlist)
//            mOrderViewmodel.paymentmode.value = "Prepaid"
//            mOrderViewmodel.paymentID.value = "$razorpayPaymentID"
//            mOrderViewmodel.doTransaction(liststring)
//        } catch (e : Exception) {
//            Log.e("MainActivity", "Exception in onPaymentSuccess", e);
//        }
//    }

//    override fun onPaymentError(code: Int, response: String?) {
//        try {
//            Toast.makeText(requireContext(),
//                "Payment failed: " + code.toString() + " " + response,
//                Toast.LENGTH_SHORT
//            ).show()
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Exception in onPaymentError", e)
//        }
//    }


}