package com.pahadi.uncle.presentation.logout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.domain.utils.SharedPrefHelper


class LogoutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation_message)
            .setPositiveButton(R.string.logout) { _, _ ->
                SharedPrefHelper.isLoggedIn = false

                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT)
                    .show()
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
                findNavController().navigate(R.id.homeScreenFragment, null, navOptions)

//              //*********for refreshing activty**********//
//                val intent = requireActivity().intent
//                intent.putExtra("logout","log")
////                intent.addFlags(
////                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
////                            or Intent.FLAG_ACTIVITY_NO_ANIMATION
////                )
//                requireActivity().overridePendingTransition(0, 0)
//                requireActivity().finish()
//
//                requireActivity().overridePendingTransition(0, 0)
//                startActivity(intent)
            }.setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }.create()
}