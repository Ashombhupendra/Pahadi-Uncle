package com.pahadi.uncle.presentation.my_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import com.pahadi.uncle.databinding.FragmentMyOrdersBinding



class My_Orders : Fragment() {
    private val mOrderViewmodel by activityViewModels<OrderViewModel>()
    private lateinit var mBinding: FragmentMyOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {
        mBinding = FragmentMyOrdersBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mOrderViewmodel.getOrderHistory()
        mOrderViewmodel.orderHistoryList.observe(viewLifecycleOwner){
             val adapter = OrderHistoryAdapter(it)
            mBinding.orderHistoryRV.adapter =adapter
            adapter.notifyDataSetChanged()
        }
    }


}