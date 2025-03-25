package com.pahadi.uncle.presentation.settings

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // **************No Internet Dialog: Signal**************//
        NoInternetDialogSignal.Builder(
            requireContext() as Activity,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }

                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
       // **************No Internet Dialog: Signal END**************//

        val settingsRV = view.findViewById<RecyclerView>(R.id.settings_rv)
        settingsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SettingsAdapter { actionId ->
                if (actionId.equals(R.id.logoutDialogFragment)) {
                    customalertdilaoglogout()
                }else if (actionId.equals(R.id.add_Address)){
                    val args = bundleOf("paymentscreen" to false)
                    findNavController().navigate(actionId,args)
                }
                else {
                    findNavController().navigate(actionId)
                }
            }
        }
    }


    private fun customalertdilaoglogout(){
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("LOGOUT")
            .setContentText("Are you sure you want to Logout...")
            .setCancelText("No")
            .setConfirmText("Logout")

            .setConfirmButton("LOGOUT", SweetAlertDialog.OnSweetClickListener {
                // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)
                it.showCancelButton(false)

                SharedPrefHelper.isLoggedIn = false
                it.setTitleText("LOGOUT").setContentText("You are successfully logout ").setConfirmText("OK")
                    .setConfirmClickListener( SweetAlertDialog.OnSweetClickListener {
                        findNavController().popBackStack(R.id.homeScreenFragment, false)
                        it.cancel()
                        it.setCanceledOnTouchOutside(false)

                    } ).changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            })
            .apply { setCanceledOnTouchOutside(false) }

            .show()


    }

}
