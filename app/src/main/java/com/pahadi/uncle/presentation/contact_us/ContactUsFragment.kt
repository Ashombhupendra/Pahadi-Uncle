package com.pahadi.uncle.presentation.contact_us

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentContactUsBinding
import com.pahadi.uncle.presentation.utils.showSnackBar

class ContactUsFragment : Fragment() {
    private val viewModel: ContactUsViewModel by viewModels()
    private lateinit var binding: FragmentContactUsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactUsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     /*   viewModel.submitOperationState.observe(viewLifecycleOwner) { networkOperationState ->
            binding.progressIndicator.isVisible = networkOperationState is NetworkOperationState.Loading
            if (networkOperationState is NetworkOperationState.Success) {
                showSnackBar("Info submitted Successfully")
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }
*/    }
}