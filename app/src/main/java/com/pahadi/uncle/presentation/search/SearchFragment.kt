package com.pahadi.uncle.presentation.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView

import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentSearchBinding
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.ProductClickListener
import com.pahadi.uncle.presentation.home.ProductsPagingAdapter
import com.pahadi.uncle.presentation.utils.hideKeyboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class SearchFragment : Fragment(), ProductClickListener {
    private lateinit var mBinding: FragmentSearchBinding
    companion object {
        private const val REQUEST_CODE_STT = 1
    }
    private val mViewModel: SearchViewModel by viewModels()
    private var searchJob: Job? = null
    private val productsPagingAdapter = ProductsPagingAdapter(this)
    var sdistrict = arrayOf(
        "Select District",
        "Almora",
        "Bageshwar",
        "Chamoli",
        "Champawat",
        "Dehradun",
        "Haridwar",
        "Nainital",
        "Pauri Garhwal",
        "Pithoragarh",
        "Rudraprayag",
        "Tehri Garhwal",
        "Udham Singh Nagar",
        "Uttarkashi"
    )
     var sDistrict : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  showKeyboard(requireContext(), view)

        mBinding.searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
               // Toast.makeText(requireContext(), sDistrict.toString(), Toast.LENGTH_SHORT).show()
              //  searchProducts(textView.text.toString())
                hideKeyboard(requireContext(), mBinding.searchEditText)
                true
            } else
                false
        }

         mBinding.googleMic.setOnClickListener {
             getSpeechtotext()
         }

        mBinding.searchDone.setOnClickListener {
            //     Toast.makeText(requireContext(), sDistrict.toString(), Toast.LENGTH_SHORT).show()
            searchProducts(mBinding.searchEditText.text.toString(), sDistrict.toString())
            hideKeyboard(requireContext(), mBinding.searchEditText)

        }
        mBinding.searchResultsRv.adapter = productsPagingAdapter
        mBinding.shimmerLayout.startShimmer()
        productsPagingAdapter.addLoadStateListener {
            mBinding.searchResultsRv.isVisible = it.source.refresh is LoadState.NotLoading
            mBinding.shimmerLayout.isVisible = it.source.refresh is LoadState.Loading
        }
        productsPagingAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mBinding.noProductsFoundMessage.isVisible =
                    itemCount == 0 && productsPagingAdapter.itemCount == 0
            }
        })

        //search spinner
         mBinding.searchDistrict.setOnTouchListener(object : View.OnTouchListener{
             override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                hideKeyboard(requireContext(), mBinding.searchDistrict)
                 return false
             }

         })
        val zdistrictsname = ProductRepository.districts.map { it.d_district }
        val districtadapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner

        ){


            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val tv : TextView = super.getDropDownView(position, convertView, parent) as TextView
                // set item text size
                //tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12F)
                // set selected item style
                if (position.toLong() == mBinding.searchDistrict.selectedItemPosition.toLong() && position != 0 ){
                         tv.background = ColorDrawable(Color.parseColor("#FF4444"))
                        tv.setTextColor(Color.parseColor("#ffffff"))

                }else{
                    tv.background = ColorDrawable(Color.parseColor("#ffffff"))
                    tv.setTextColor(Color.parseColor("#000000"))

                }


                return tv
            }
        }

        districtadapter.add("Select District")
        districtadapter.add("All District")
        districtadapter.addAll(zdistrictsname.asReversed())

        mBinding.searchDistrict.adapter = districtadapter
        mBinding.searchDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val conItem = districtadapter.getItem(position)
                val indexs = zdistrictsname.indexOf(conItem)
                if (indexs >=2){
                    //  view?.background = ColorDrawable(Color.parseColor("#FF8500"))
                 //   Toast.makeText(requireContext(), conItem  + indexs, Toast.LENGTH_SHORT).show()
                             sDistrict = conItem.toString()
                    // sellProductViewModel.selectconditionID.value = conItem
                }else{
                    sDistrict= ""
                }
              
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {


            }


        }

    }

    private fun searchProducts(query: String, distrit: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            mViewModel.searchProduct(query, distrit)
                .observe(viewLifecycleOwner, Observer<PagingData<ProductDto>> {
                    productsPagingAdapter.submitData(lifecycle, it)
                })
        }
    }

    override fun onProductClick(itemView: View, productDto: ProductDto, index: Int) {
        if (SharedPrefHelper.isLoggedIn) {
            val args = bundleOf("ProductDetails" to ProductMapper.toProductDetails(productDto))
            val extras =
                FragmentNavigatorExtras(itemView to "$BASE_URL/uploads/product/${productDto.productImage}")
            findNavController().navigate(
                R.id.productDetailsFragment,
                args,
                null,
                extras
            )
        } else {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            REQUEST_CODE_STT -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
                         mBinding.searchEditText.setText(recognizedText)
                    }
                }
            }
        }
    }
    fun getSpeechtotext(){
        // Get the Intent action
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // Adding an extra language, you can use any language from the Locale class.
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        // Text that shows up on the Speech input prompt.
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
        try {
            // Start the intent for a result, and pass in our request code.
            startActivityForResult(sttIntent, REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
            Toast.makeText(requireContext(), "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }
}
