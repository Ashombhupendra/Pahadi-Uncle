package com.pahadi.uncle.presentation.sell_product

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentUploadVideoBinding
import com.pahadi.uncle.domain.utils.FileUtils
import com.pahadi.uncle.presentation.MainActivity
import com.pahadi.uncle.presentation.login.NetworkState
import com.pahadi.uncle.presentation.utils.showSnackBar
import java.io.File
import java.lang.Long

class UploadVideoFragment : Fragment() {
    private lateinit var mBinding: FragmentUploadVideoBinding
    private val sellProductViewModel by activityViewModels<SellProductViewModel>()
    private var uri: Uri? = null
    private lateinit var path: String
   lateinit var progressDialog: ProgressDialog
   val Bprogress = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUploadVideoBinding.inflate(layoutInflater, container, false)
        mBinding.frag = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindProgressButton(mBinding.nextButton)
        progressDialog = ProgressDialog(activity)
        val actionBar: ActionBar = (activity as MainActivity).getSupportActionBar()!!
        actionBar.setDisplayHomeAsUpEnabled(false)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */)
                 {
                override fun handleOnBackPressed() {
            // Handle the back button event
                    MaterialAlertDialogBuilder(PahadiUncleApplication.instance.applicationContext)
                        .setTitle("Image Cancellation")
                        .setMessage("If You Back Your Images will be removed.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                         findNavController().navigate(R.id.uploadPhotoFragment)
                            sellProductViewModel.images.clear()
                            sellProductViewModel.imagelist.clear()
                        }
                        .setNegativeButton("No"){ cancel, _ ->
                            cancel.dismiss()

                        }

                        .show()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        if (!sellProductViewModel.imagelist.isNullOrEmpty()){

            sellProductViewModel.imagelist.forEachIndexed { index, imageData ->
                Log.d("Productdetails", sellProductViewModel.imagelist.toString())

                when(imageData.imageView){
                    0 -> {
                        Glide.with(requireContext()).load(imageData.uri).into(mBinding.uploadImage1)
                    }
                    1 -> {
                        Glide.with(requireContext()).load(imageData.uri).into(mBinding.uploadImage2)
                    }
                    2 -> {
                        Glide.with(requireContext()).load(imageData.uri).into(mBinding.uploadImage3)
                    }
                    3 -> {
                        Glide.with(requireContext()).load(imageData.uri).into(mBinding.uploadImage4)
                    }
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
                    showSnackBar(sellProductViewModel.error)
                }
                NetworkState.SUCCESS -> {
                    requireActivity().viewModelStore.clear()
                    productaddedsuccessfullydialog()
//                    showSnackBar("Post Added Successfully")
//                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeScreenFragment, true).build()
//                    findNavController().navigate(R.id.homeScreenFragment, null, navOptions)
                }
            }
        }
    }

    fun getVideo() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "video/*"
        }
        startActivityForResult(intent, 12)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == AppCompatActivity.RESULT_OK) {
            val uri = data?.data

            val size = getSize(PahadiUncleApplication.instance.applicationContext, uri)
            val sizeinmb = size!!.toInt() / 1024 /1024


            Log.d("size", size.toString())
              if (sizeinmb < 15){
            uri?.let {
                this.uri = uri

                playVideo(uri)

            }
              }else{
                  showSnackBar("Vedio size should be less than 15MB ")
              }
        }
    }

    private fun playVideo(uri: Uri) {
        val mediaController = MediaController(requireContext()).apply {
            setAnchorView(mBinding.videoView)
        }
        mBinding.videoView.apply {
            visibility = View.VISIBLE
            mBinding.videoViewPlaceholder.visibility = View.GONE
            setMediaController(mediaController)
            setVideoURI(uri)
            requestFocus()
            start()
        }
    }

    fun submit() {


        uri?.let { u ->
            requireContext().contentResolver.openInputStream(u)?.let {
                val name = FileUtils.getFile(requireContext(), u).name
                val bytes = it.readBytes()
                sellProductViewModel.video = bytes to name
            }
        }

      //  sellProductViewModel.addProduct()
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

    fun getSize(context: Context, uri: Uri?): String? {
        var fileSize: String? = null
        val cursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null, null)!!
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex)
                }
            }
        } finally {
            cursor.close()
        }
        return fileSize
    }



}
