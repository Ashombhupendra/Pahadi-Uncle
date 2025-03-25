package com.pahadi.uncle.presentation.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.button.MaterialButton
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.presentation.home.HomeScreenFragment
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.launch


class NotificationView : Fragment() {
    private val requestCall = 1

    private var phonenumbre : String = ""
    private var emailaddress : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_view, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = view.findViewById<TextView>(R.id.tv_notifi_view) as TextView
        val name = view.findViewById<TextView>(R.id.notification_view_name) as TextView
        val email = view.findViewById<TextView>(R.id.notification_view_email) as TextView
        val message = view.findViewById<TextView>(R.id.notification_view_message) as TextView
        val phone = view.findViewById<TextView>(R.id.notification_view_phone) as TextView
        val showproduct = view.findViewById<TextView>(R.id.show_product) as TextView
        val chat = view.findViewById<MaterialButton>(R.id.notification_view_chat_button) as MaterialButton

        val bundle = arguments
        if (bundle != null){

            email.text = bundle.getString("email")
            date.text = bundle.getString("date")
            phone.text = bundle.getString("phone")
            emailaddress = bundle.getString("email").toString()
            phonenumbre = bundle.getString("phone").toString()



            val notificationfrom = bundle.getString("noitifcation_type")
            if (notificationfrom.equals("admin")){
                    name.text = "Admin"
                message.text =  bundle.getString("message")

                phone.visibility = View.GONE
                showproduct.visibility = View.GONE
                email.visibility = View.GONE
                chat.visibility = View.GONE
            }else if (notificationfrom.equals("user")) {
                val nadfasf : String = bundle.getString("message")?.substring(8).toString()
                val nuifsdn :String =
                    nadfasf?.replace("Product ", "",false)?.replace("Name ","",false).toString()

                name.text = bundle.getSerializable("title").toString()
                message.text =  nuifsdn

                phone.visibility = View.VISIBLE
                email.visibility = View.VISIBLE
                showproduct.visibility = View.VISIBLE
                val img = requireContext().resources.getDrawable(R.drawable.show_interest_32)
                name.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

                val senderid = bundle.getString("userid").toString()
                val profile_pic = bundle.getString("profile_pic").toString()
                val username = bundle.getString("title").toString()
                chat.visibility = View.VISIBLE

                chat.setOnClickListener {
                    val args = bundleOf(
                        "other_person_user_id" to senderid,
                        "other_person_name" to username,
                        "other_person_profile_image" to profile_pic
                    )
                    findNavController().navigate(R.id.action_global_sendMessageFragment, args)
                }


                if (!emailaddress.isNullOrEmpty()){
                    email.setOnClickListener {
                        sendEmail(emailaddress)
                    }
                }





            }

            phone.setOnClickListener {
                makePhoneCall(phonenumbre)
            }


            showproduct.setOnClickListener {
                showproduct.apply {
                    showProgress()
                    isEnabled = false
                }
                lifecycleScope.launch {
                    val product =
                        ProductRepository.getsingleproduct(bundle.getString("productid", "0"))
                    when (product) {
                        is ResultWrapper.Success -> {
                            showproduct.apply {
                                hideProgress("Show Product")
                                isEnabled = true
                            }
                            Log.d("Main", product.response.toString())
                            val args = bundleOf(
                                "ProductDetails" to ProductMapper.toProductDetails(
                                    product.response
                                )
                            )
                            findNavController().navigate(R.id.productDetailsFragment, args)
                        }
                        is ResultWrapper.Failure -> {
                            Log.d("Main", product.errorMessage)
                            showproduct.apply {
                                hideProgress("Show Product")
                                isEnabled = true
                            }
                        }
                    }

                }
            }

        }
    }

    private fun makePhoneCall(number : String) {

        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                    PahadiUncleApplication.instance.applicationContext,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                   requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    requestCall
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(requireContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == requestCall) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(phonenumbre)
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun sendEmail(recipient: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))



        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }

    }

}