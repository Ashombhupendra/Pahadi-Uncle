package com.pahadi.uncle.presentation.home

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentHomeScreenBinding
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.AuthRepository
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.Slider.SliderAdapter
import com.pahadi.uncle.presentation.home.Slider.SliderItem
import com.pahadi.uncle.presentation.home.categories.CategoryViewGroup
import com.pahadi.uncle.presentation.utils.showSnackBar
//import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal


class HomeScreenFragment : Fragment(), ProductClickListener , ProductFavClick {


    private val mViewModel by activityViewModels<HomeViewModel>()
    private lateinit var mBinding: FragmentHomeScreenBinding
    private var job: Job? = null
    private val productsPagingAdapter = ProductsPagingAdapter(this, null, this)
    private val featuredproductsPagingAdapter = ProductsPagingAdapter(this, null, this)

    private var scrollToPosition = 0
    private var initialCategorySelected = false


    val list = ArrayList<SliderItem>()
 companion object{
     var sellerlocationstatus = false
     var scrollposition = 0
 }


    private fun loadProducts(categoryId: Int) {

        job?.cancel()
        job = lifecycleScope.launch {

            if (SharedPrefHelper.isLoggedIn){
                val userid = SharedPrefHelper.user.userId
                mViewModel.userID.value = userid
            }
            else{
                mViewModel.userID.value= "0"
            }
            mViewModel.getFeaturedProduct(categoryId)
            mViewModel.getProducts(categoryId).collectLatest {
                mViewModel.progressboolean.value = false
                productsPagingAdapter.submitData(it)

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentHomeScreenBinding.inflate(layoutInflater, container, false)
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this
        mBinding.frag = this

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getSliderImage()
        mViewModel.getDIstrict()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }


           mViewModel.progressboolean.observe(viewLifecycleOwner){
               if (it){
                   mBinding.pBar.visibility = View.VISIBLE
               }else{
                   mBinding.pBar.visibility = View.GONE
               }
           }


        if (!mViewModel.shareid.value.isNullOrEmpty() && !mViewModel.shareid.value.equals("-1")){


            lifecycleScope.launch {
                val product = ProductRepository.getsingleproduct(mViewModel.shareid.value.toString())
                when(product){
                    is ResultWrapper.Success -> {
                        val itemView : View
                        mViewModel.shareid.value = "-1"
                        Log.d("Main", product.response.toString())
                        val args = bundleOf(
                            "ProductDetails" to ProductMapper.toProductDetails(
                                product.response
                            )
                        )

                        val navigationData = NavigationData(
                            destinationId = R.id.action_homeScreenFragment_to_productDetailsFragment,
                            args = args,
                            extras = null

                        )
                        navigatetoproductdetail(navigationData)
                    }
                    is ResultWrapper.Failure -> {
                        Log.d("Main", product.errorMessage)
                    }
                }
            }




        }
        mBinding.includedHeader.featuredProductsRv.layoutManager = GridLayoutManager(context, 2)
        mBinding.includedHeader.featuredProductsRv.adapter = featuredproductsPagingAdapter

        mViewModel.getfeaturedProductslist.observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                mBinding.includedHeader.featuredProductTv.visibility = View.GONE
                mBinding.includedHeader.featuredProductsRv.visibility = View.GONE
            }else{
                mBinding.includedHeader.featuredProductTv.visibility = View.VISIBLE
                mBinding.includedHeader.featuredProductsRv.visibility = View.VISIBLE
            }
            featuredproductsPagingAdapter.submitData(lifecycle, PagingData.from(it))

        }


        //******CHAT ALERT*****////
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (SharedPrefHelper.isLoggedIn) {
                    if (PahadiUncleApplication.instance.isConnectedToInternet()) {
                        val user = SharedPrefHelper.user
                        lifecycleScope.launch {
                            val result = ChatRepository.chatalert(user.userId)
                            when (result) {
                                is ResultWrapper.Success -> {
                                    val status = result.response.status
                                    val message = result.response.message
                                    Log.d("CHAT", status.toString() + message.toString())
                                    if (status.equals("true") && message.equals("enable")) {

                                        mBinding.chatalert.visibility = View.VISIBLE


                                    } else {
                                        mBinding.chatalert.visibility = View.INVISIBLE

                                    }


                                }
                                is ResultWrapper.Failure -> {
                                    Log.d("userstatus",result.errorMessage.toString())

                                }
                            }
                        }

                        lifecycleScope.launch {
                            val userID = SharedPrefHelper.user.userId
                            val seller = AuthRepository.getSellerDetails(userID)
                            when(seller){
                                is ResultWrapper.Success ->{
                                    if (seller.response.status.equals(true)){
                                        sellerlocationstatus = true

                                    }else{
                                        sellerlocationstatus = false

                                    }
                                }
                                is ResultWrapper.Failure ->{
                                        Log.d("error", seller.errorMessage)
                                }
                            }

                        }
                        lifecycleScope.launch {
                            val userID = SharedPrefHelper.user.userId
                            val status = AuthRepository.getUserStatus(userID)
                            when(status){
                                is ResultWrapper.Success ->{
                                       Log.d("userstatus",status.response.toString())
                                    if (status.response.status.equals("FALSE")){
                                        SharedPrefHelper.isLoggedIn = false
                                    }


                                }
                                is ResultWrapper.Failure ->{
                                    Log.d("userstatus",status.errorMessage.toString())
                                }
                            }

                        }


                    } else {

                    }
                }

//                handler.postDelayed(this, 1000)
            }
        }, 1000)




//            mViewModel.sliderlist1.observe(requireActivity(), Observer {
//                val adapter = SliderAdapter(it)
//
//                val slider = view.findViewById<SliderView>(R.id.slider)
//
//                slider.setSliderAdapter(adapter)
//                slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
//                /// sliderView?.setSliderAdapter(adapter)
//                slider.setScrollTimeInSec(5);
//                slider.setAutoCycle(true);
//                slider.startAutoCycle();
//                adapter.notifyDataSetChanged()
//
//            })
        //for hiding keyboard
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        productsPagingAdapter.apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    val count = productsPagingAdapter.itemCount
                    Log.d("PRODUCTADAPTER", count.toString() + ":" + itemCount.toString())
                    //     mBinding.noCribsMessage.isVisible = count == 0 && itemCount == 0
                    mBinding.includedHeader.noCribsMessage.isVisible = count == 0 && itemCount == 0

                }
            })
        }

        //for Internet Connection
        Nointernet()

        exitTransition = MaterialElevationScale(false).apply {
            duration = 400
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 400
        }
        enterTransition = MaterialFadeThrough().apply {
            duration = 400
        }

        mBinding.productsRv.layoutManager = GridLayoutManager(context, 2)
        mBinding.productsRv.adapter = productsPagingAdapter


         mViewModel.cateitem.value = -1
        mBinding.includedHeader.categoryRv.categoryClickedListener =
            object : CategoryViewGroup.OnCategoryClickedListener {
                override fun onCategoryClicked(categoryId: Int) {
                    mViewModel.progressboolean.value = true

                    productsPagingAdapter.refresh()
                    featuredproductsPagingAdapter.refresh()
                    loadProducts(categoryId)

                    mViewModel.cateitem.value = categoryId

                }
            }
        mViewModel.getCategories()

        mViewModel.categories.observe(viewLifecycleOwner) { categories ->
            mBinding.includedHeader.categoryRv.setCategories(categories)
            if(!initialCategorySelected){
                mBinding.includedHeader.categoryRv.selectIndex(0)


                initialCategorySelected = true
            }
        }


        mBinding.includedHeader.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_searchFragment)
        }
        mBinding.includedHeader.googleMic.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_searchFragment)
        }
        mBinding.wallShare.setOnClickListener {
            if (mViewModel.cateitem.value != -1) {
                findNavController().popBackStack(R.id.homeScreenFragment, true)
                findNavController().navigate(R.id.homeScreenFragment)
            }
        }
        mBinding.settings.setOnClickListener {
            val navigationData = NavigationData(
                destinationId = R.id.action_homeScreenFragment_to_settingsFragment
            )
            navigate(navigationData)
        }

        mBinding.account.setOnClickListener {
            if (sellerlocationstatus.equals(true)){
                val navigationData = NavigationData(
                    destinationId =  R.id.profileFragment
                )
                navigate(navigationData)
            }else{
                val navigationData = NavigationData(
                    destinationId =  R.id.sellerLocationFragment
                )
                navigate(navigationData)
            }
        }

        mBinding.chat.setOnClickListener {

            val navigationData = NavigationData(
                destinationId = R.id.chatListFragment
            )
            navigate(navigationData)
        }
    }

    fun onFABClicked(view: View) {


        if (sellerlocationstatus.equals(true)){
            val extras = FragmentNavigatorExtras(view to "circular_reveal")

            val navigationData = NavigationData(
                destinationId = R.id.action_homeScreenFragment_to_sellProductFragment,
                extras = extras
            )
            navigate(navigationData)
        }else{
            val navigationData = NavigationData(
                destinationId =  R.id.sellerLocationFragment
            )
            navigate(navigationData)
        }
    }

    override fun onProductClick(itemView: View, productDto: ProductDto, index: Int) {
        val args = bundleOf("ProductDetails" to ProductMapper.toProductDetails(productDto))
        val extras =
            FragmentNavigatorExtras(itemView to "$BASE_URL/uploads/product/${productDto.productImage}")
        val navigationData = NavigationData(
            destinationId = R.id.action_homeScreenFragment_to_productDetailsFragment,
            args = args,
            extras = extras
        )
        navigatetoproductdetail(navigationData)

        val layoutManager = mBinding.productsRv.layoutManager as GridLayoutManager
       scrollToPosition = layoutManager.findFirstVisibleItemPosition()
    }

    private fun navigatetoproductdetail(navigationData: NavigationData){
        val nav = findNavController()
        if (nav.currentDestination?.id == R.id.homeScreenFragment){
            nav.navigate(
                navigationData.destinationId,
                navigationData.args,
                navigationData.navOptions,
                navigationData.extras
            )
        }
    }

    private fun navigate(navigationData: NavigationData) {
        val navController = findNavController()
        if (SharedPrefHelper.isLoggedIn) {
            if (navController.currentDestination?.id == R.id.homeScreenFragment) {
                navController.navigate(
                    navigationData.destinationId,
                    navigationData.args,
                    navigationData.navOptions,
                    navigationData.extras
                )
            }
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }

    data class NavigationData(
        val destinationId: Int,
        val args: Bundle? = null,
        val navOptions: NavOptions? = null,
        val extras: Navigator.Extras? = null
    )
 

    // ***************No Internet Dialog: Signal**************//

    private fun Nointernet(){
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

    override fun onPause() {
        super.onPause()

    }
    override fun onResume() {
        super.onResume()

         if(scrollToPosition != 0) {
            mBinding.productsRv.scrollToPosition(scrollToPosition)
            mBinding.appBar.setExpanded(false)
             mBinding.pBar.visibility = View.GONE
        }
    }

}
