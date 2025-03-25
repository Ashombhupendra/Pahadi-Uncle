package com.pahadi.uncle.presentation.product_details.image_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.network.data.ImageDto
import kotlinx.coroutines.launch


data class listofilmage(
    val imagename: String
)

class ImageDetail : Fragment() {
    val sliderlist1 = MutableLiveData<List<listofilmage>>()

    var images  = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("thisisimage", images.toString())
        val viewpager = view.findViewById<ViewPager>(R.id.viewPager_id)
        val viewpagercustom = view.findViewById<CustomViewPager>(R.id.custom_viewpager)


        val bundle = arguments
        if (bundle != null){
            val productid = bundle.getString("productID")
         //   showSnackBar("product id : $productid")

            lifecycleScope.launch {
                val result = ProductRepository.getProductImageslist(productid.toString())

                when(result){
                    is ResultWrapper.Success -> {
                        val sliderList = mutableListOf<listofilmage>()
                        sliderList.addAll(result.response.map { toImagelisst(it) })

                        sliderlist1.value = sliderList

                    }
                    is ResultWrapper.Failure -> {

                    }
                }



            }
        }


        sliderlist1.observe(requireActivity(), Observer {
            Log.d("thisisimag", it.toString())
            val adapter = DetailProductImagesAdapter(
                PahadiUncleApplication.instance.applicationContext,
                it
            )


            viewpagercustom.adapter = adapter

        })


    }


    private fun toImagelisst(imageDto: ImageDto) = listofilmage(
        imageDto.productImageName
    )




}