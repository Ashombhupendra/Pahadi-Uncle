package com.pahadi.uncle.presentation.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentBuyerProfileBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.utils.*
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModel
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModelFactory
import com.pahadi.uncle.presentation.utils.showSnackBar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import org.json.JSONObject
import java.io.*

class BuyerProfileFragment : Fragment(R.layout.fragment_buyer_profile) {
    private lateinit var binding: FragmentBuyerProfileBinding
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var viewModel: SellerInformationViewModel
    private var picture: Pair<String, ByteArray>? = null
    private var selectedImageUri: Uri? = null
    private var imagename: String? = null
    private var byte: ByteArray? = null
    val sellerid = MutableLiveData<String>()
    lateinit var pictureMultipart: MultipartBody
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuyerProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // (SharedPrefHelper.agentLoginStatus as AgentLoginStatus.LoggedIn).agentDto
        val buyer = (SharedPrefHelper.user)
        AndroidNetworking.initialize(requireContext());
        bindProgressButton(binding.buyerSubmitPro)

        //   binding.buyerusername = buyer.userName
          // binding.buyerphone = buyer.phoneNumber


        val viewModelFactory = SellerInformationViewModelFactory(buyer.userId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            SellerInformationViewModel::class.java
        )
        viewModel.sellerDetails.observe(viewLifecycleOwner) {

            if (it.status.equals(true)) {
                sellerid.value = it.sellerDto.profileId
                binding.sellerDetails = it.sellerDto
                Log.d("sellerdetail3", it.sellerDto.toString())
                binding.phone.setText(buyer.phoneNumber)
            }else{
                sellerid.value = ""
                binding.username.setText(buyer.userName)
                binding.phone.setText(buyer.phoneNumber)
                binding.email.setText(buyer.email)
                Picasso.get().load(buyer.profile_image).into(binding.buyerProPic)

            }
        }

        Log.d("User_data_name", buyer.phoneNumber)

        // ***************No Internet Dialog: Signal**************//

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



        binding.buyerProPic.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.mBottomSheetDialogTheme)


            val bottomsheetview = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.bottom_sheet_layout,
                    view.findViewById(R.id.bottom_sheet)
                ) as LinearLayout




            bottomsheetview.findViewById<View>(R.id.bottom_sheet_camera).setOnClickListener {
                checkPermession(0)
            }
            bottomsheetview.findViewById<View>(R.id.bottom_sheet_gallery).setOnClickListener {
                checkPermession(1)
            }

            bottomSheetDialog.setContentView(bottomsheetview)
            bottomSheetDialog.show()

        }

        binding.buyerSubmitPro.setOnClickListener {
            val username = view.findViewById<EditText>(R.id.username)
            val email = view.findViewById<EditText>(R.id.email)
            val phone = view.findViewById<EditText>(R.id.phone)
                if (picture != null){
                    val user = SharedPrefHelper.user
                    SharedPrefHelper.user = UserEntity(
                        userId = user.userId,
                        sellerId = user.sellerId,
                        phoneNumber = user.phoneNumber ,
                        userName = user.userName,
                        profile_image = selectedImageUri.toString(),
                        email = user.email
                    )
                }
            binding.buyerSubmitPro.apply {
                showProgress()
                isEnabled = false
            }
            Log.d("Resultd", imagename + " :" + byte)
            //    updatebuyerpro(picture,username.text.toString(), email.text.toString(), phone.text.toString())
            val buyer1 = (SharedPrefHelper.user)

            Log.d("Result : ", "userid = ${buyer1.userId} , sellerid = ${buyer1.sellerId}")
            lifecycleScope.launch {
            //    val sellerResponse = AuthRepository.getSellerDetails(buyer1.userId)
                val result = AuthRepository.buyerprofileupdate(
                    sellerid.value.toString(), buyer1.userId, username.text.toString(), email.text.toString(),
                    phone.text.toString(), picture
                )
                when (result) {
                    is ResultWrapper.Success -> {
                        Log.d("Result", result.response.toString())
                      //  showSnackBar("Profile Successfully Updated.. ")
                        binding.buyerSubmitPro.apply {
                            hideProgress("Submit")
                            isEnabled = true
                        }
                        //reloading the current screen, so that observer is added to crib live data


                        productInterestDialog()
                    }
                    is ResultWrapper.Failure -> {
                        Log.d("Result f", result.errorMessage)
                        showSnackBar("Something Went Wrong Try  Again Later.. ")

                        binding.buyerSubmitPro.apply {
                            hideProgress("Submit")
                            isEnabled = true
                        }
                    }
                }
            }

        }

    }

    fun capturePhoto() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 12)
    }

    fun gallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            this.type = "image/*"
        }
        startActivityForResult(intent, 11)

    }

   
    // Message to be shown if user denies access and possibly send him to the settings
    private fun showRationalDialogForPermissions() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(
            requireContext(),
            com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Alert
        )
        // set alert dialog message text color
        alertDialog.setTitle("Need Permissions")
        val message =
            SpannableString("This app needs permission to use this feature. You can grant them in app settings.")
        message.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "GO TO SETTINGS"
        ) { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", "com.pahadi.uncle", null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        alertDialog.setNegativeButton(
            "CANCEL"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 12 && data != null) {
            selectedImageUri = getImageUri(data.extras?.get("data") as Bitmap)
            selectedImageUri?.let { uploadNewImage(it) }
            binding.buyerProPic.setImageURI(selectedImageUri)

        }
        if (resultCode == Activity.RESULT_OK && requestCode == 11 && data != null) {
            val uri = data?.data
            uri?.let {
                selectedImageUri = it
                uploadNewImage(it)
            }
            binding.buyerProPic.setImageURI(data?.data)
        }
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val wrapper = ContextWrapper(PahadiUncleApplication.instance.applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"P_${System.currentTimeMillis()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            inImage.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return Uri.fromFile(file)
    }

    fun checkPermession(num: Int) {
        Dexter.withContext(requireContext()).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        if (num == 0) {
                            capturePhoto()
                            bottomSheetDialog.dismiss()

                        } else {
                            // Toast.makeText(requireContext(), "Select Image from Gallery.. ", Toast.LENGTH_SHORT).show()
                            gallery()
                            bottomSheetDialog.dismiss()

                        }
                    } else {
                        showRationalDialogForPermissions()
                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()

    }

    private fun uploadNewImage(uri: Uri) {
        requireContext().contentResolver.openInputStream(uri)?.let {
            val bytes = it.readBytes()
            val name = FileUtils.getFile(requireContext(), uri).name
            Log.d("Resultd", imagename + " :" + byte)

            picture = Pair(name, bytes)

        }
    }

    fun productInterestDialog(){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_interst_dialog, null)
        //AlertDialogBuilder
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)
        text.setText("Profile Successfully Updated.. ")
        btn.setOnClickListener {
            findNavController().navigate(R.id.homeScreenFragment)
            mAlertDialog.cancel()
        }
    }


}