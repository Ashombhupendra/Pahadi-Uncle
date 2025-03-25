package com.pahadi.uncle.presentation.my_cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentMyCartBinding
import com.pahadi.uncle.databinding.FragmentMyOrdersBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.OrderHistoryRepository
import com.pahadi.uncle.network.data.MyCartDTO
import com.pahadi.uncle.presentation.my_orders.OrderViewModel
import com.pahadi.uncle.presentation.my_orders.SelectProdcutDTO
import kotlinx.coroutines.launch


class My_Cart : Fragment(), onCartClick {

    private val mOrderViewmodel by activityViewModels<OrderViewModel>()
    private lateinit var mBinding: FragmentMyCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMyCartBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mOrderViewmodel.getMyCart()
        mOrderViewmodel.mycartlist.observe(viewLifecycleOwner){
            val adapter = MyCartAdapter(it, this)
            mBinding.mycartRv.adapter = adapter
            adapter.notifyDataSetChanged()

            if (!it.isNullOrEmpty()){
                mOrderViewmodel.myPlaceOrderPrice.value = 0
               it.forEach {
                   val amount = it.price.toInt() * it.quantity.toInt()
                   mOrderViewmodel.myPlaceOrderPrice.value = mOrderViewmodel.myPlaceOrderPrice.value!!.toInt() + amount
               }
            }
        }

        mOrderViewmodel.myPlaceOrderPrice.observe(viewLifecycleOwner){
            mBinding.placeyourorderPrice.text = "₹ $it"
        }


        mBinding.placeOrder.setOnClickListener {

             mOrderViewmodel.mycartlist.value!!.forEach {
                  mOrderViewmodel.selectProductlist.add(SelectProdcutDTO(it.product_id, it.quantity))
                }
            mOrderViewmodel.orderamount.value = mBinding.placeyourorderPrice.text.toString()
            findNavController().navigate(R.id.paymentScreen)

        }



    }

    override fun onCartItemClick(cartDTO: MyCartDTO) {

        lifecycleScope.launch {
            val result = OrderHistoryRepository.deleteMyCart(cartDTO.id)
            when(result){
                is ResultWrapper.Success ->{
                    mOrderViewmodel.myPlaceOrderPrice.value = 0
                    mOrderViewmodel.getMyCart()

                }
                is ResultWrapper.Failure ->{

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }
    override fun onResume() {
        super.onResume()
        mOrderViewmodel.myPlaceOrderPrice.value = 0
    }

    override fun onCartQuantityChange(cartDTO: MyCartDTO, quantity: Int) {


        lifecycleScope.launch {
            val result = OrderHistoryRepository.addToCartProduct(cartDTO.product_id, quantity.toString())
            when(result){
                is ResultWrapper.Success ->{
                    mOrderViewmodel.myPlaceOrderPrice.value = 0
                    mOrderViewmodel.getMyCart()

                }
                is ResultWrapper.Failure ->{

                }
            }
        }
    }


}