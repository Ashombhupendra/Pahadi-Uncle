package com.pahadi.uncle.presentation.seller_information

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.pahadi.uncle.databinding.FragmentSellerInformationBinding
import java.util.*

class SellerInformationFragment : Fragment() {
    private lateinit var binding: FragmentSellerInformationBinding
    private lateinit var viewModel: SellerInformationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = requireArguments().getString("user_id")!!
        val viewModelFactory = SellerInformationViewModelFactory(userId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            SellerInformationViewModel::class.java
        )
        viewModel.sellerDetails.observe(viewLifecycleOwner){
            binding.sellerDetails = it.sellerDto
            Log.d("sellerdetail2", it.sellerDto.toString())
        }
        /// val finname: String = message.substring(0,1).toUpperCase(Locale.getDefault()) + message.substring(1)


    }
}