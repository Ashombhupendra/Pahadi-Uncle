package com.pahadi.uncle.presentation.product_details.sellerproducts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentSellerProductListBinding
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.*
import com.pahadi.uncle.presentation.product_details.ProductDetailsViewModel
import kotlinx.coroutines.launch


class Seller_Product_list : Fragment(), ProductClickListener, ProductFavClick {
        val    mProductviewmodel by activityViewModels<HomeViewModel>()
    private lateinit var mBinding : FragmentSellerProductListBinding
   private  val productsPagingAdapter = ProductsPagingAdapter(this, null , this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSellerProductListBinding.inflate(layoutInflater, container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


              val userid = arguments?.getString("user_id")
        mProductviewmodel.getSellerProduct(userid.toString())

        mProductviewmodel.isLoading.observe(viewLifecycleOwner){
            if (it){
                mBinding.messageProgressIndicator.visibility = View.VISIBLE
            }else{
                mBinding.messageProgressIndicator.visibility = View.GONE
            }
        }

        mBinding.sellerProductRv.adapter = productsPagingAdapter
        mProductviewmodel.sellerproducts.observe(viewLifecycleOwner){
                productsPagingAdapter.submitData(lifecycle, PagingData.from(it))
        }
    }

    override fun onProductClick(itemView: View, productDto: ProductDto, index: Int) {
        val args = bundleOf("ProductDetails" to ProductMapper.toProductDetails(productDto))
        val extras =
            FragmentNavigatorExtras(itemView to "$BASE_URL/uploads/product/${productDto.productImage}")
        findNavController().navigate(R.id.productDetailsFragment, args, null, extras)

    }

    override fun onFavClicked(itemView: View, productDto: ProductDto) {
        itemView.isClickable = false


        if (SharedPrefHelper.isLoggedIn){
            val userid = SharedPrefHelper.user.userId
            lifecycleScope.launch {
                val product = ProductMapper.toProductDetails(productDto)
                //   showSnackBar("${product}")
                val result = ProductRepository.saveWishList(userid, productId = product.productId)
                when(result){
                    is ResultWrapper.Success ->{

                        /* findNavController().popBackStack(R.id.homeScreenFragment, true)
                         findNavController().navigate(R.id.homeScreenFragment)*/
                        productsPagingAdapter.refresh()
                        itemView.isClickable = true
                        Log.d("Wishlist", result.response.toString())
                    }
                    is ResultWrapper.Failure ->{
                        Log.d("Wishlist", result.errorMessage)
                        itemView.isClickable = true

                    }
                }
            }
        }else{
            findNavController().navigate(R.id.loginFragment)
        }
    }


}