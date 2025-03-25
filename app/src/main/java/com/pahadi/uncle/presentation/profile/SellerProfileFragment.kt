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
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentSellerProfileBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.utils.FileUtils
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.log
import com.pahadi.uncle.network.AuthService
import com.pahadi.uncle.network.data.PinCodeResponse
import com.pahadi.uncle.network.data.SellerResponse
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModel
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModelFactory
import com.pahadi.uncle.presentation.utils.hideKeyboard
import com.pahadi.uncle.presentation.utils.showSnackBar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import org.json.JSONArray
import java.io.*

class SellerProfileFragment : Fragment(R.layout.fragment_seller_profile) {
    private lateinit var binding: FragmentSellerProfileBinding
    private val authService = getRetrofitService(AuthService::class.java)
    private lateinit var viewModel: SellerInformationViewModel
    private lateinit var mProfileViewModel: ProfileViewModel
    private val job = Job()
    lateinit var postresponse : PinCodeResponse
    val sellerid = MutableLiveData<String>()
    val REQUEST_CODE = 200
    private var picture: Pair<String, ByteArray>? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    private var selectedImageUri: Uri? = null
    val citypos  = MutableLiveData<String>()

    override fun getContext(): Context? {
        return super.getContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buyer = (SharedPrefHelper.user)
        bindProgressButton(binding.btnSellerEditPro)
        // val text = view.findViewById<TextView>(R.id.username)
        AndroidNetworking.initialize(requireContext());

        //  Toast.makeText(requireContext(), "SELLER" + buyer.sellerId, Toast.LENGTH_SHORT).show()

        // ***************No Internet Dialog: Signal**************//


        Nointernet()
        val viewModelFactory = SellerInformationViewModelFactory(buyer.userId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            SellerInformationViewModel::class.java
        )

        viewModel.sellerDetails.observe(viewLifecycleOwner) {
            if(it.status.equals(true)) {
                sellerid.value = it.sellerDto.profileId
                binding.sellerDetails = it.sellerDto
                citypos.value = it.sellerDto.city.toString()
                Log.d("sellerdetail4", it.sellerDto.toString())
            }else{
                binding.username.setText(buyer.userName)
                sellerid.value =  ""
                binding.email.setText(buyer.email)
                Picasso.get().load(buyer.profile_image).into(binding.sellerProEditImg)
            }
        }
          binding.pincode.apply {
              addTextChangedListener {
                  if (it!!.length > 4){
                      lifecycleScope.launch {
                          val result = AuthRepository.getPostOffice(it.toString())
                          viewModel.pincode.value = it.toString()
                          Log.d("pincoderesult", result.toString())
                            viewModel.district.value = ""
                            viewModel.state.value = ""

                          // mProfileViewModel.pincode.value = it.toString()
                          if (!result?.city.equals(null)){

                              viewModel.district.value= result?.district
                              viewModel.state.value = result?.state

                              //mProfileViewModel.district.value = result?.district
                              //mProfileViewModel.state.value = result?.state

                              binding.state.text = result?.state?.toEditable()
                              binding.district.text = result?.district?.toEditable()
                              binding.proCity.setOnTouchListener(object : View.OnTouchListener{
                                  override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                                      hideKeyboard(requireContext(), binding.proCity)
                                      return false
                                  }

                              })
                              val conditionadapter =object : ArrayAdapter<String>(
                                  requireContext(),
                                  R.layout.item_spinner,
                                  result!!.gpoList

                              ){
                                  override fun getDropDownView(
                                      position: Int,
                                      convertView: View?,
                                      parent: ViewGroup
                                  ): View {
                                      val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                                      // set item text size


                                      if (position.toLong() == binding.proCity.selectedItemPosition.toLong() && position != 0 ){
                                          tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                                          tv.setTextColor(Color.parseColor("#ffffff"))

                                      }else{
                                          tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                                          tv.setTextColor(Color.parseColor("#000000"))

                                      }



                                      return tv
                                  }
                              }

                              binding.proCity.adapter = conditionadapter
                              binding.proCity.setSelection(conditionadapter.getPosition(citypos.value.toString()))
                              binding.proCity.onItemSelectedListener = object :
                                  AdapterView.OnItemSelectedListener{
                                  override fun onItemSelected(
                                      parent: AdapterView<*>?,
                                      view: View?,
                                      position: Int,
                                      id: Long
                                  ) {
                                      val conItem = conditionadapter.getItem(position)
                                      val indexs = result?.gpoList?.indexOf(conItem)
                                      if (indexs !=0){
                                          //  Toast.makeText(requireContext(), conItem  + indexs,Toast.LENGTH_SHORT).show()
                                          viewModel.city.value = conItem
                                          //   mProfileViewModel.city.value = conItem
                                      }
                                  }
                                  override fun onNothingSelected(parent: AdapterView<*>?) {
                                      TODO("Not yet implemented")
                                  }


                              }

                          }

                          else{
                              Log.d("pincodeerror", result.toString())
                          }
                      }

                  }


              }
          }


        view.findViewById<MaterialButton>(R.id.btn_seller_edit_pro).setOnClickListener {
            val text = view.findViewById<EditText>(R.id.username)
            val email = view.findViewById<EditText>(R.id.email)
            val building = view.findViewById<EditText>(R.id.building)
            val location = view.findViewById<EditText>(R.id.location)
            val landmark = view.findViewById<EditText>(R.id.landmark)
            val pincode = view.findViewById<EditText>(R.id.pincode)
            val state = view.findViewById<EditText>(R.id.state)
            val city = view.findViewById<EditText>(R.id.city)
            val district = view.findViewById<EditText>(R.id.district)

            val user = SharedPrefHelper.user


            if (building.text.isNullOrEmpty()){
                building.setError("Enter Building Number")
            }else if (location.text.isNullOrEmpty()){
                location.setError("Enter your location")
            }else if (landmark.text.isNullOrEmpty()){
                landmark.setError("Enter your landmark")
            }else if (pincode.text.isNullOrEmpty()){
                pincode.setError("Enter your pin number")
            }else{
                updateprofile(
                    text.text.toString(),
                    viewModel.district.value ?: "",
                    user.phoneNumber.toString(),
                    email.text.toString(),
                    building.text.toString(),
                    location.text.toString(),
                    landmark.text.toString(),
                    viewModel.pincode.value ?: "",
                    viewModel.state.value ?: "",
                    viewModel.city.value ?: ""
                )

            }


        }
        ///*******for profile picture************//
        binding.sellerProEditImg.setOnClickListener {
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
    }

    fun updateprofile(
        username: String, district: String, phone: String, email: String, building: String,
        location: String, landmark: String, Pincode: String,
        state: String, city: String
    ) {

        binding.btnSellerEditPro.apply {
            showProgress()
            isEnabled = false
        }


        lifecycleScope.launch() {

            val buyer = (SharedPrefHelper.user)


            val result = AuthRepository.updateSellerDetails(
                buyer.userId, sellerid.value.toString(),
                userName = username, email = email, buildingNumber = building, location = location,
                landmark = landmark, state = state, district = district, city = city, pinCode = Pincode,
                picture = picture
            )
            Log.d("RESULT", result.toString())
            when (result) {
                is ResultWrapper.Success -> {
                    Log.d("Result", result.response.toString())
                    binding.btnSellerEditPro.apply {
                        hideProgress("Submit")
                        isEnabled = true
                    }
                    //reloading the current screen, so that observer is added to crib live data
                      productInterestDialog()

                }
                is ResultWrapper.Failure -> {
                    Log.d("Result f", result.errorMessage)
                    binding.btnSellerEditPro.apply {
                        hideProgress("Submit")
                        isEnabled = true
                    }
                    showSnackBar("Something Went Wrong Try  Again Later.. ")
                }


            }
        }

    }




    // ***************No Internet Dialog: Signal**************//
    private fun Nointernet() {
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


    }


    fun capturePhoto() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            //seller_pro_edit_img.setImageBitmap(data.extras?.get("data") as Bitmap)
            selectedImageUri = getImageUri(data.extras?.get("data") as Bitmap)
            selectedImageUri?.let { uploadNewImage(it) }
            binding.sellerProEditImg.setImageURI(selectedImageUri)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 11 && data != null) {
            val uri = data.data
            uri?.let {
                selectedImageUri = it
                uploadNewImage(it)
            }
            binding.sellerProEditImg.setImageURI(data.data)

        }
    }

    fun getImageUri( inImage: Bitmap): Uri? {
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

            picture = Pair(name, bytes)

        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

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