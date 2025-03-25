package com.pahadi.uncle.presentation.agent.PasswordChange

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentAgentPasswordChangeBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AgentRepository
import com.pahadi.uncle.domain.utils.AgentLoginStatus
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.launch


class AgentPasswordChange : Fragment() {
    lateinit var newpass: String
    lateinit var confirmpass: String
   var  pshow :Int = 0
    lateinit var binding : FragmentAgentPasswordChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgentPasswordChangeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
           bindProgressButton(binding.btnChangePass)
        val newpassword = view.findViewById<EditText>(R.id.agent_new_password)
        val confirmpassword = view.findViewById<EditText>(R.id.agent_confirm_password)
        val agent = (SharedPrefHelper.agentLoginStatus as AgentLoginStatus.LoggedIn).agentDto


        newpassword.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= newpassword.getRight() - newpassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if (pshow == 0) {
                        newpassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        pshow++
                    }else{
                        newpassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        pshow--

                    }
                    Toast.makeText(requireContext(), " Success", Toast.LENGTH_SHORT).show()
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.btnChangePass.setOnClickListener {

            newpass = newpassword.text.toString()
            confirmpass = confirmpassword.text.toString()
            if (newpass.isEmpty()) {
                newpassword.setError("Create Password")
            }else if(newpassword.length() <= 7){
                 newpassword.setError("Minimum length Shoud have 7 Characters")
            }
            else if (confirmpass.isEmpty()) {
                confirmpassword.setError("Enter Previous  Password")
            } else if (newpass.equals(confirmpass)) {
                //showSnackBar("Successfully update  Password"+ agent.id )
                submitpassword(agent.id, newpass)
                binding.btnChangePass.apply {
                    showProgress()
                    isEnabled = false
                }
            } else {
                failedcustomalertdilaoglogout("Enter Correct Password")
            }

        }
    }

     private fun submitpassword(userid : String , password:String){
         lifecycleScope.launch {

             val result = AgentRepository.agentchangepass(userid, password)
           when(result){
               is  ResultWrapper.Success -> {
                   Log.d("CHANGEPASSWORD", result.response.toString())
                   binding.btnChangePass.apply {
                       hideProgress("Submit")
                       isEnabled = true
                   }
                   if (result.response.status.equals("true")){
                       customalertdilaoglogout(result.response.message)

                   }else{
                       failedcustomalertdilaoglogout("Password Not Changed \n try again....")
                   }
               }
               is ResultWrapper.Failure ->{

                   failedcustomalertdilaoglogout("Password Not Changed \n try again....")
                   Log.d("CHANGEPASSWORD 12", result.errorMessage)
                   binding.btnChangePass.apply {
                       hideProgress("Submit")
                       isEnabled = true
                   }

               }
           }
         }
     }

    private fun customalertdilaoglogout(message : String){
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success")
            .setContentText(message)
            .setConfirmText("OK")

            .setConfirmButton("OK", SweetAlertDialog.OnSweetClickListener {
                // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)
                it.showCancelButton(false)

                findNavController().navigate(R.id.agentDashboardFragment)
                it.dismiss()
            })
            .apply { setCanceledOnTouchOutside(false) }

            .show()


    }

    private fun failedcustomalertdilaoglogout(message : String){
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed")
            .setContentText(message)
            .setConfirmText("OK")

            .setConfirmButton("OK", SweetAlertDialog.OnSweetClickListener {
                // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)
                it.showCancelButton(false)

              //  findNavController().navigate(R.id.agentDashboardFragment)
                it.dismiss()
            })
            .apply { setCanceledOnTouchOutside(false) }

            .show()


    }

}