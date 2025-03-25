package com.pahadi.uncle.presentation.sell_product

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentUploadPhotoBinding
import com.pahadi.uncle.domain.utils.FileUtils
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.utils.showSnackBar
import java.io.*
import java.util.*


private data class ImageData(val imageView: ImageView, var uri: Uri?)
data class saveImageData(val imageView: Int, var uri: Uri?)
private const val STORAGE_PERMISSION_CODE = 114


class UploadPhotoFragment : Fragment() {
    private lateinit var mBinding: FragmentUploadPhotoBinding
    private val sellProductViewModel by activityViewModels<SellProductViewModel>()
    private val images = mutableListOf<ImageData>()
    lateinit var bottomSheetDialog: BottomSheetDialog
    private var index = -1



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUploadPhotoBinding.inflate(layoutInflater, container, false)
        mBinding.frag = this

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProgressButton(mBinding.nextButton)
        images.add(ImageData(mBinding.image1, null))
        images.add(ImageData(mBinding.image2, null))
        images.add(ImageData(mBinding.image3, null))
        images.add(ImageData(mBinding.image4, null))

        //if the user is editing product instead of creating
        if (sellProductViewModel.productId != null) {
            sellProductViewModel.getImages().observe(viewLifecycleOwner) { imageList ->
                val imageViewList = listOf(
                    mBinding.image1,
                    mBinding.image2,
                    mBinding.image3,
                    mBinding.image4
                )
                imageList.forEachIndexed { index, url ->
                    Glide.with(requireContext()).load(url).into(imageViewList[index])
                }
            }
        }


        sellProductViewModel.sellProductState.observe(viewLifecycleOwner) {
            when (it) {
                NetworkState.LOADING_STARTED -> {
                    mBinding.nextButton.apply {
                        isEnabled = false
                        showProgress()
                    }
                }
                NetworkState.LOADING_STOPPED -> {
                    mBinding.nextButton.apply {
                        isEnabled = true
                        hideProgress("submit")
                    }
                }
                NetworkState.FAILED -> {
                    productuploadfailed()
                }
                NetworkState.SUCCESS -> {
                    requireActivity().viewModelStore.clear()
                 //   productaddedsuccessfullydialog()

                    productInterestDialog()
//                    showSnackBar("Post Added Successfully")
//                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()
//                    findNavController().navigate(R.id.homeScreenFragment, null, navOptions)
                }
            }
        }


        mBinding.cancelImg1.setOnClickListener {
            Log.d(
                "PRoductdetailslist",
                sellProductViewModel.imagelist.toString() + ":" + sellProductViewModel.images.toString()
            )
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Image")
                .setMessage("Are you sure you want remove Image")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    if (!sellProductViewModel.imagelist.isNullOrEmpty()) {
                        showSnackBar("Image Removed ")
                       sellProductViewModel.imagelist.forEachIndexed { index, saveImageData ->
                           if (index.equals(0)){
                               sellProductViewModel.imagelist.remove(sellProductViewModel.imagelist[index])
                           }
                       }
                        mBinding.image1.setImageDrawable(null)
                    }


                }
                .setNegativeButton("No"){ cancel, _ ->
                    cancel.dismiss()

                }
                .show()
        }
        mBinding.cancelImg2.setOnClickListener {
                Log.d(
                    "PRoductdetailslist",
                    sellProductViewModel.imagelist.toString() + ":" + sellProductViewModel.images.toString()
                )
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Remove Image")
                    .setMessage("Are you sure you want remove Image")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()

                        if (!sellProductViewModel.imagelist.isNullOrEmpty()) {
                            sellProductViewModel.imagelist.forEachIndexed { index, imagedata ->
                                when(imagedata.imageView){
                                    1 -> {

                                        //
                                        sellProductViewModel.imagelist.remove(sellProductViewModel.imagelist[index])
                                        Log.d("Productdetailsss", "this")
                                        mBinding.image2.setImageDrawable(null)
                                        showSnackBar("Image Removed ")
                                    }
                                }

                            }
                        }


                    }
                    .setNegativeButton("No") { cancel, _ ->
                        cancel.dismiss()

                    }
                    .show()


            }
        mBinding.cancelImg3.setOnClickListener {
            Log.d(
                "PRoductdetailslist",
                sellProductViewModel.imagelist.toString() + ":" + sellProductViewModel.images.toString()
            )
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Image")
                .setMessage("Are you sure you want remove Image")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    if (!sellProductViewModel.imagelist.isNullOrEmpty()) {
                        sellProductViewModel.imagelist.forEachIndexed { index, imagedata ->
                            when(imagedata.imageView){
                                2 -> {
                                    sellProductViewModel.imagelist.remove(sellProductViewModel.imagelist[index])
                                    Log.d("Productdetailsss", "this")
                                    mBinding.image3.setImageDrawable(null)
                                    showSnackBar("Image Removed ")
                                }
                            }

                        }


                    }

                }
                .setNegativeButton("No") { cancel, _ ->
                    cancel.dismiss()

                }
                .show()

        }
        mBinding.cancelImg4.setOnClickListener {
            Log.d(
                "PRoductdetailslist",
                sellProductViewModel.imagelist.toString() + ":" + sellProductViewModel.images.toString()
            )
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Image")
                .setMessage("Are you sure you want remove Image")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    if (!sellProductViewModel.imagelist.isNullOrEmpty()) {
                       sellProductViewModel.imagelist.forEachIndexed { index, imagedata ->
                           when(imagedata.imageView){
                               3 -> {
                                   sellProductViewModel.imagelist.remove(sellProductViewModel.imagelist[index])
                                   Log.d("Productdetailsss", "this")
                                   mBinding.image4.setImageDrawable(null)
                                   showSnackBar("Image Removed ")
                               }
                           }

                       }


                    }

                }
                .setNegativeButton("No") { cancel, _ ->
                    cancel.dismiss()

                }
                .show()






        }
    }

    fun getImageFromGallery(index: Int) {
        this.index = index

        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.mBottomSheetDialogTheme)


        val bottomsheetview = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_layout,
                view?.findViewById(R.id.bottom_sheet)
            ) as LinearLayout




        bottomsheetview.findViewById<View>(R.id.bottom_sheet_camera).setOnClickListener {
                  checkPermession(0, index)
        }
        bottomsheetview.findViewById<View>(R.id.bottom_sheet_gallery).setOnClickListener {
                  checkPermession(1, index)
        }

        bottomSheetDialog.setContentView(bottomsheetview)
        bottomSheetDialog.show()

    /*    when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                //permission is already granted
                getImage(index)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                //telling user why the permission is required
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permission Required")
                    .setMessage(R.string.image_permission_rationale)
                    .setNeutralButton("OK") { dialog, _ ->
                        requestStoragePermission()
                        dialog.dismiss()
                    }.setNegativeButton("No Thanks") { dialog, _ ->
                        //closing the rationale dialog
                        dialog.dismiss()
                    }.show()
            }
            else -> {
                //user has not granted permission yet, so requesting for the permission
                requestStoragePermission()
            }
        }

     */


    }

    private fun requestStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            STORAGE_PERMISSION_CODE
        )
    }
    fun capturePhoto(index: Int) {
        this.index = index


        val cameraIntent = Intent("android.media.action.IMAGE_CAPTURE")


        startActivityForResult(cameraIntent, 12)
    }

    private fun getImage(index: Int) {

        this.index = index
        val intent = Intent(Intent.ACTION_PICK).apply {
            this.type = "image/*"
        }
        startActivityForResult(intent, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode ==11) {
            val uri = data?.data
            uri ?: return
            val selectedImageView = images[index]
            selectedImageView.uri = uri
            sellProductViewModel.imagelist.add(
                saveImageData(
                    index, uri
                )
            )
            Log.d("Productdetails", selectedImageView.imageView.toString())
            Glide.with(requireContext()).load(uri).into(selectedImageView.imageView)
        }
        else if (resultCode == AppCompatActivity.RESULT_OK && requestCode ==12) {
            try {
                val uri =getImageUri(data!!.extras!!.get("data") as Bitmap)
                uri ?: return
                val selectedImageView = images[index]
                selectedImageView.uri = uri

                sellProductViewModel.imagelist.add(
                    saveImageData(
                        index, uri
                    )
                )
               // val selectedImageView = images[index]
                   selectedImageView.imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
               // Glide.with(requireContext()).load(uri).into(selectedImageView.imageView)

            }catch (e: IOException){
                Toast.makeText(requireContext(), "Exception...${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("photoupload","Exception...${e.message}" )


            } catch (e: FileNotFoundException) {
                Toast.makeText(requireContext(), "Exception...${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("photoupload","Exception...2${e.message}" )
            }

        }else{
            Toast.makeText(
                requireContext(),
                "Something went wrong. Try again...$resultCode",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun getImageUri(inImage: Bitmap): Uri? {

        val wrapper = ContextWrapper(PahadiUncleApplication.instance.applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"P_${System.currentTimeMillis()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream:OutputStream = FileOutputStream(file)
            inImage.compress(Bitmap.CompressFormat.JPEG,80,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return Uri.fromFile(file)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getImage(index)
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.permission_denied)
                        .setMessage(R.string.storage_permission_denied_message)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            findNavController().popBackStack(R.id.homeScreenFragment, true)
                        }.show()
                }
                return
            }
        }
    }

    fun submit() {

        images.forEach { imageData ->
            imageData.uri?.let { uri ->
                requireActivity().contentResolver.openInputStream(uri)?.let { inputStream ->
                    val name = FileUtils.getFile(requireContext(), uri).name
                    val bytes = inputStream.readBytes()
                    sellProductViewModel.images.add(bytes to name)
                }

            }
        }

        if (hasAtLeastOneImage())
           // findNavController().navigate(R.id.action_uploadPhotoFragment_to_uploadVideoFragment)
            sellProductViewModel.addProduct()

        else
            showSnackBar("Select at least one Image")
    }

    private fun hasAtLeastOneImage(): Boolean {
        images.forEach {
            if (it.uri != null) {
                return true
            }
        }
        return false
    }

    fun checkPermession(num: Int, index: Int) {
        Dexter.withContext(requireContext()).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        if (num == 0) {
                            capturePhoto(index)
                            bottomSheetDialog.dismiss()

                        } else {
                            // Toast.makeText(requireContext(), "Select Image from Gallery.. ", Toast.LENGTH_SHORT).show()
                            getImage(index)
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

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()




    }
    private fun productaddedsuccessfullydialog(){
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success")
            .setContentText("Your Product was Added Successfully..")

            .setConfirmText("Done")
            .showCancelButton(false)
            .setConfirmButton("Done", SweetAlertDialog.OnSweetClickListener {
                // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()
                findNavController().navigate(R.id.homeScreenFragment, null, navOptions)
                it.cancel()

            })
            .apply { setCanceledOnTouchOutside(false) }
            .show()


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
        text.setText("Product Successfully Uploaded.. ")
        btn.setOnClickListener {
            findNavController().navigate(R.id.homeScreenFragment)
            mAlertDialog.cancel()
        }
    }

    fun productuploadfailed(){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_error_dialog, null)
        //AlertDialogBuilder
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)


        text.setText("Product Failed to Upload, Retry.. ")
        btn.setOnClickListener {

            mAlertDialog.cancel()
        }
    }



}
