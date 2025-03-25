package com.pahadi.uncle.presentation.sell_product

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.google.gson.Gson
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentSellProductBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.UserEntityMapper
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.*
import com.pahadi.uncle.presentation.home.categories.CategoryItem
import com.pahadi.uncle.presentation.utils.hideKeyboard
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.launch
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal


class SellProductFragment : Fragment() {
    private lateinit var mBinding: FragmentSellProductBinding
    private val sellProductViewModel by activityViewModels<SellProductViewModel>()
    var languages = arrayOf("Select Product Condition", "Brand New", "Used", "Fresh","Live", "Dry")
    var district = arrayOf("Select District", "Almora", "Bageshwar","Chamoli","Champawat","Dehradun", "Haridwar",
                 "Nainital" ,"Pauri Garhwal" ,"Pithoragarh", "Rudraprayag", "Tehri Garhwal","Udham Singh Nagar","Uttarkashi")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val transformation = MaterialContainerTransform().apply {
//            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
//            duration = 400
//        }
//        transformation.drawingViewId = R.id.fragment
//        sharedElementEnterTransition = transformation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSellProductBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = sellProductViewModel
        mBinding.frag = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    //    mBinding.scrollView.transitionName = "circular_reveal"

          ///***************No Internet connection method ******************///////////
        Nointernet()

         Log.d("SELLERDETAIL", "")


        val productDtoString = requireArguments().getString("product_dto")
        if (!productDtoString.isNullOrBlank()) {
            val productDto = Gson().fromJson(productDtoString, ProductDto::class.java)
            sellProductViewModel.setDefaultValues(productDto)
        }


        ///****************For condition Spinner ******************///////////
        mBinding.productCondition.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard(requireContext(), mBinding.productCondition)
                return false
            }

        })
         val conditionadapter =object :ArrayAdapter<String>(
             requireContext(),
             R.layout.item_spinner,
             languages

         ){
             override fun getDropDownView(
                 position: Int,
                 convertView: View?,
                 parent: ViewGroup
             ): View {
                 val tv :TextView = super.getDropDownView(position, convertView, parent) as TextView
                 // set item text size

                 if (position.toLong() == mBinding.productCondition.selectedItemPosition.toLong() && position != 0 ){
                     tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                     tv.setTextColor(Color.parseColor("#ffffff"))

                 }else{
                     tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                     tv.setTextColor(Color.parseColor("#000000"))

                 }



                 return tv
             }
         }
        mBinding.productCondition.adapter = conditionadapter
        mBinding.productCondition.setSelection(conditionadapter.getPosition(sellProductViewModel.selectconditionID.value.toString()))

        mBinding.productCondition.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val conItem = conditionadapter.getItem(position)
                val indexs = languages.indexOf(conItem)
                if (indexs !=0){
                  //  Toast.makeText(requireContext(), conItem  + indexs,Toast.LENGTH_SHORT).show()
                    sellProductViewModel.selectconditionID.value = conItem
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }

        ///****************For district Spinner ******************///////////

        mBinding.productDistrict.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard(requireContext(), mBinding.productDistrict)
                return false
            }

        })
        val zdistrictsname = ProductRepository.districts.map { it.d_district }

        val districtadapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val tv :TextView = super.getDropDownView(position, convertView, parent) as TextView
                // set item text size

                if (position.toLong() == mBinding.productDistrict.selectedItemPosition.toLong() && position != 0 ){
                    tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                    tv.setTextColor(Color.parseColor("#ffffff"))

                }else{
                    tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                    tv.setTextColor(Color.parseColor("#000000"))

                }



                return tv
            }
        }
        Log.d("DIstricts",zdistrictsname.toString())


        districtadapter.add("Select District")
        districtadapter.addAll(zdistrictsname.asReversed())
        mBinding.productDistrict.adapter = districtadapter

        Log.d("selectdistric", "${sellProductViewModel.selectDistrict.value}, ${sellProductViewModel.selectedCategoryId}")
        mBinding.productDistrict.setSelection(districtadapter.getPosition(sellProductViewModel.selectDistrict.value.toString()))

        mBinding.productDistrict.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val conItem = districtadapter.getItem(position)
                val indexs = zdistrictsname.indexOf(conItem)
                if (indexs !=0){
                  //  view?.background = ColorDrawable(Color.parseColor("#FF8500"))
                    //  Toast.makeText(requireContext(), conItem  + indexs,Toast.LENGTH_SHORT).show()

                    sellProductViewModel.selectDistrict.value = conItem
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }



        }

        ///****************For category Spinner ******************///////////
        mBinding.category.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard(requireContext(), mBinding.category)
                return false
            }

        })
        val categoryNames = ProductRepository.categories.map { it.categoryName }
        Log.d("DIstricts",categoryNames.toString())
        val arrayAdapter = object  :ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val tv :TextView = super.getDropDownView(position, convertView, parent) as TextView
                // set item text size

                if (position.toLong() == mBinding.category.selectedItemPosition.toLong() && position != 0 ){
                    tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                    tv.setTextColor(Color.parseColor("#ffffff"))

                }else{
                    tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                    tv.setTextColor(Color.parseColor("#000000"))

                }
                return tv
            }
        }
        arrayAdapter.add("Select Category")
        arrayAdapter.addAll(categoryNames)
        mBinding.category.adapter = arrayAdapter


        sellProductViewModel.editcategoryboolean.observe(viewLifecycleOwner){
            if (it){
                sellProductViewModel.editcategoryboolean.value = false
                mBinding.category.setSelection(arrayAdapter.getPosition(sellProductViewModel.editcategory.value.toString()))
            }
        }


        mBinding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val item = arrayAdapter.getItem(position)
                val index = categoryNames.indexOf(item)
                if (index != -1) {
                    sellProductViewModel.selectedCategoryId =
                        ProductRepository.categories[index].id
               //     Toast.makeText(requireContext(), item  + index,Toast.LENGTH_SHORT).show()

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun submit() {
        val priceHasError = sellProductViewModel.price.value.isNullOrEmpty()
        val productNameHasError = sellProductViewModel.productName.value.isNullOrEmpty()
        val descriptionHasError = sellProductViewModel.description.value.isNullOrEmpty()
        val productunitHasError = sellProductViewModel.productUnit.value.isNullOrEmpty()
        val  category = sellProductViewModel.selectedCategoryId
        val  condition = sellProductViewModel.selectconditionID.value.isNullOrEmpty()
        val  productcity = sellProductViewModel.productcity.value.isNullOrEmpty()

        if (priceHasError || productNameHasError ) {
            showSnackBar("Please fill all entries")

        }else if (productcity){
            mBinding.city.setError("City Can't be Empty")
        }
        else if (productunitHasError){

            mBinding.unit.setError("Unit Can't be Empty")
        }
        else if(category.equals(-1)){
            showSnackBar("Please Select Category")
            val selecttext : TextView = mBinding.category.selectedView as TextView
            selecttext.setError("Please Select Category")

        }else if(condition){
            showSnackBar("Please Select Condition")
            val selecttext : TextView = mBinding.productCondition.selectedView as TextView
            selecttext.setError("Please Select Category")
        }else if (descriptionHasError){
            val selecttext : EditText = mBinding.description as EditText
            selecttext.setError("Description Can Not be Empty")
        }
        else {
            findNavController().navigate(R.id.action_sellProductFragment_to_uploadPhotoFragment)
        }
    }


    ///****************Check Internet Connection  ******************///////////
    private fun Nointernet(){
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

    }

}