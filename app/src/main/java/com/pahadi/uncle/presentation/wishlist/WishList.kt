package com.pahadi.uncle.presentation.wishlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.HomeScreenFragment
import com.pahadi.uncle.presentation.home.ProductClickListener
import com.pahadi.uncle.presentation.home.ProductFavClick
import com.pahadi.uncle.presentation.home.ProductsPagingAdapter
import com.pahadi.uncle.presentation.home.Slider.SliderItem
import com.pahadi.uncle.presentation.utils.showSnackBar
import kotlinx.coroutines.launch


class WishList : Fragment() , ProductClickListener,ProductFavClick {

    private lateinit var productwishListRv: RecyclerView
    private val productsPagingAdapter = ProductsPagingAdapter(this, null, this)
    val sliderlist1 = MutableLiveData<List<ProductDto>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productwishListRv = view.findViewById(R.id.product_wishlist_rv)

        productwishListRv.adapter = productsPagingAdapter
        productwishListRv.layoutManager = GridLayoutManager(requireContext(), 2)

        lifecycleScope.launch {
            val userid = SharedPrefHelper.user.userId
            val result = ProductRepository.getwishList(userid)
            when(result){
                is ResultWrapper.Success ->{
                    val sliderList = mutableListOf<ProductDto>()

                    sliderList.addAll(result.response.map { it })

                    sliderlist1.value = sliderList
                }
                is ResultWrapper.Failure ->{
                    Log.d("Wishlistthis", result.errorMessage)
                    val wishtv  = view.findViewById<TextView>(R.id.pro_wishlist_no_pro)
                    wishtv.visibility = View.VISIBLE
                }
            }
        }

        sliderlist1.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                 showSnackBar("No products found")


                return@observe
            }
            productsPagingAdapter.submitData(lifecycle, PagingData.from(it))
        }


    }

    override fun onProductClick(itemView: View, productDto: ProductDto, index: Int) {
        val args = bundleOf("ProductDetails" to ProductMapper.toProductDetails(productDto))
        val extras =
                FragmentNavigatorExtras(itemView to "$BASE_URL/uploads/product/${productDto.productImage}")
        val navigationData = NavigationData(
                destinationId = R.id.action_wishlist_to_productDetailsFragment,
                args = args,
                extras = extras
        )
        navigatetoproductdetail(navigationData)


    }

    override fun onFavClicked(itemView: View, productDto: ProductDto) {
        if (SharedPrefHelper.isLoggedIn){
            val userid = SharedPrefHelper.user.userId
            lifecycleScope.launch {
                val product = ProductMapper.toProductDetails(productDto)
                //   showSnackBar("${product}")
                val result = ProductRepository.saveWishList(userid, productId = product.productId)
                when(result){
                    is ResultWrapper.Success ->{
                        findNavController().popBackStack(R.id.wishList, true)
                        findNavController().navigate(R.id.wishList)

                        productsPagingAdapter.refresh()
                        Log.d("Wishlist", result.response.toString())
                    }
                    is ResultWrapper.Failure ->{
                        Log.d("Wishlist", result.errorMessage)

                    }
                }
            }
        }else{
            findNavController().navigate(R.id.loginFragment)
        }
    }
    private fun navigatetoproductdetail(navigationData: NavigationData){
        val nav = findNavController()
        if (nav.currentDestination?.id == R.id.wishList){
            nav.navigate(
                    navigationData.destinationId,
                    navigationData.args,
                    navigationData.navOptions,
                    navigationData.extras
            )
        }
    }

    data class NavigationData(
            val destinationId: Int,
            val args: Bundle? = null,
            val navOptions: NavOptions? = null,
            val extras: Navigator.Extras? = null
    )

}