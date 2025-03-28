 package com.pahadi.uncle.presentation.product_details

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.AddToCartBottomSheetBinding
import com.pahadi.uncle.databinding.FragmentProductDetailsBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.OrderHistoryRepository
import com.pahadi.uncle.domain.repositories.ProductRepository
import com.pahadi.uncle.domain.repositories.RatingRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.RatingDTO
import com.pahadi.uncle.presentation.MainActivity
import com.pahadi.uncle.presentation.rating.ViewAllRatingAdapter
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModel
import com.pahadi.uncle.presentation.seller_information.SellerInformationViewModelFactory
import com.pahadi.uncle.presentation.utils.showSnackBar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.DialogInterface

import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.pahadi.uncle.presentation.my_orders.OrderViewModel
import com.pahadi.uncle.presentation.my_orders.SelectProdcutDTO
import com.pahadi.uncle.presentation.utils.temp_showToast


 class ProductDetailsFragment : Fragment(), rvHost {
    private lateinit var viewModel: SellerInformationViewModel

    private val mOrderViewmodel by activityViewModels<OrderViewModel>()
        val seller_number = MutableLiveData<String>()
    val ratinglist = MutableLiveData<List<RatingDTO>>()
    var counter = 1
     var addtocartBool : Boolean = false
    private val mProductDetails by lazy {
        requireArguments().getParcelable<ProductDetails>("ProductDetails")!!
    }
    private val mProductDetailsViewModel by viewModels<ProductDetailsViewModel> {
        ProductDetailsViewModelFactory(mProductDetails.productId)
    }
    private lateinit var mBinding: FragmentProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SharedPrefHelper.isLoggedIn) {
            setHasOptionsMenu(true)
        }else{
            setHasOptionsMenu(false)
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            drawingViewId = R.id.fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProductDetailsBinding.inflate(layoutInflater, container, false).run {
        mBinding = this
        viewModel = mProductDetailsViewModel
        lifecycleOwner = this@ProductDetailsFragment
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
               if(SharedPrefHelper.isLoggedIn){
                   mProductDetailsViewModel.getInterestStatus()
                   mProductDetailsViewModel.getRatingStatus()

               }
        val viewModelFactory = SellerInformationViewModelFactory(mProductDetails.userId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            SellerInformationViewModel::class.java
        )
        viewModel.sellerDetails.observe(viewLifecycleOwner){
            mBinding.sellerDetails = it.sellerDto
            seller_number.value = it.sellerDto.phone
        }

        Log.d("ImageUrl", mProductDetails.mainImageUrl)
      //  Glide.with(requireContext()).load(mProductDetails.mainImageUrl).into(mBinding.placeholderImage);




       mProductDetailsViewModel.ratingstatus.observe(viewLifecycleOwner){
           if (it){
               mBinding.rateProduct.visibility = View.GONE

           }else{
               mBinding.rateProduct.visibility =View.VISIBLE
           }
       }

        if (SharedPrefHelper.isLoggedIn){
            val userid = SharedPrefHelper.user.userId
            if (userid.equals(mProductDetails.userId)){
                mBinding.rateProduct.visibility = View.GONE
            }else{
                mBinding.rateProduct.visibility = View.VISIBLE

            }

        }

        mBinding.productDetails = mProductDetails

         mBinding.ratingDetailCount.text = "${mProductDetails.ratings} | ( ${mProductDetails.total_rating} )"
        mBinding.ratingAverage.text = "${mProductDetails.ratings} | ( ${mProductDetails.total_rating} )"
        //adding transition name for shared element transition
        mBinding.topSection.transitionName = mProductDetails.mainImageUrl
        mBinding.productdetailFav.setOnClickListener {
                  if (mProductDetails.wishlist){
                      mBinding.productdetailFav.setImageResource(R.drawable.un_fav)
                      mProductDetails.wishlist = false
                  }else{
                      mBinding.productdetailFav.setImageResource(R.drawable.fav)
                      mProductDetails.wishlist = true

                  }
            savewishlist()
        }
        mBinding.viewAllSellerProduct.setOnClickListener {
           val bundle = Bundle()
            bundle.putString("user_id", mProductDetails.userId)
           findNavController().navigate(R.id.seller_Product_list, bundle)
        }


        //animating the bottom section to translate up
        mBinding.bottomSection.apply {
            visibility = View.VISIBLE
            ObjectAnimator.ofFloat(mBinding.bottomSection, "translationY", 200f, 0f).apply {
                duration = 400
                start()
            }
        }

        mBinding.share.setOnClickListener {
            val activity = requireActivity()
            if (activity is MainActivity) {
                activity.shareproducts("https://www.pahadiuncle.com/welcome/productdetail/${mProductDetails.productId}")

            }
        }

        mBinding.previewPane.onThumbnailClicked = { position ->
            mBinding.imagesViewPager.setCurrentItem(position, true)
        }

        mBinding.imagesViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position < 1) {
                    mBinding.previewPane.setSelected(position)
                }
            }
        })

        mProductDetailsViewModel.productMedia.observe(viewLifecycleOwner) {
            mBinding.placeholderImage.visibility = View.GONE
            mBinding.imagesViewPager.apply {
                visibility = View.VISIBLE
                adapter = ImageVideoAdapter(it, this@ProductDetailsFragment)
            }
            loadPreviews(it)
        }
        if(SharedPrefHelper.isLoggedIn) {
            val userid = SharedPrefHelper.user.userId
            if (userid.equals(mProductDetails.userId)) {
                mBinding.chatButton.visibility = View.GONE
                mBinding.showInterestButton.visibility = View.GONE
                mBinding.bottomSection.visibility  =View.GONE
            }
        }

        mProductDetailsViewModel.interested.observe(viewLifecycleOwner, Observer {

            if (it){
                if (mProductDetailsViewModel.booleaninterst){
                productInterestDialog()
                }
                mBinding.showInterestButton.apply {
                    alpha = 0.3f
                    isEnabled = false
                }
            }else{
                mBinding.showInterestButton.apply {
                    alpha = 1f
                    isEnabled = true
                }
            }


        })
        mBinding.showInterestButton.setOnClickListener {
            if (SharedPrefHelper.isLoggedIn){
               // android:onClick="@{()->viewModel.updateInterest()}"
                mProductDetailsViewModel.updateInterest()
            }else{
                findNavController().navigate(R.id.loginFragment)
            }
        }

         mBinding.rateProduct.setOnClickListener {
             if (SharedPrefHelper.isLoggedIn){
                 // android:onClick="@{()->viewModel.updateInterest()}"
              //   Toast.makeText(requireContext(),  "rate product", Toast.LENGTH_SHORT).show()
                  val bundle = Bundle()
                 bundle.putString("product_id", mProductDetails.productId)
                 findNavController().navigate(R.id.rateProduct, bundle)
             }else{
                 findNavController().navigate(R.id.loginFragment)
             }
         }
        getRatings(mProductDetails.productId)
        ratinglist.observe(viewLifecycleOwner){
            val adapter = ViewAllRatingAdapter(it as ArrayList<RatingDTO>)
            mBinding.viewRatingRv.adapter = adapter
            adapter.notifyDataSetChanged()
        }


        mBinding.viewRatingAllReviews.setOnClickListener {
            if (SharedPrefHelper.isLoggedIn){
                // android:onClick="@{()->viewModel.updateInterest()}"
               // Toast.makeText(requireContext(),  "View all rating", Toast.LENGTH_SHORT).show()

                val bundle = Bundle()
                bundle.putString("product_id", mProductDetails.productId)
                findNavController().navigate(R.id.viewAllrating, bundle)
            }else{
                findNavController().navigate(R.id.loginFragment)
            }
        }

        mBinding.callSeller.setOnClickListener {
            if (SharedPrefHelper.isLoggedIn) {


//              if (mProductDetails.featured){
//                  addtoCartItem()
//                  mProductDetailsViewModel.addtoCartPro.value = false
//              }else{

                  val intent = Intent(Intent.ACTION_DIAL)
                  intent.data = Uri.parse("tel:${seller_number.value}")
                  Log.d("sellernumber : ", seller_number.value.toString())
                  startActivity(intent)
            //  }


            }else{
                findNavController().navigate(R.id.loginFragment)

            }
        }

        mBinding.chatButton.setOnClickListener {

            if(SharedPrefHelper.isLoggedIn) {
//                if (mProductDetails.featured){
//                  //  Toast.makeText(requireContext(),"Add to cart",Toast.LENGTH_SHORT).show()
//                   //Bottom add to cart
//                    addtoCartItem()
//                    mProductDetailsViewModel.addtoCartPro.value = true
//
//                }else{
                    val args = bundleOf(
                        "other_person_user_id" to mProductDetails.userId,
                        "other_person_name" to mProductDetails.username,
                        "other_person_profile_image" to mProductDetails.profileImage,
                        "product_id" to mProductDetails.productId

                    )
                    findNavController().navigate(R.id.action_global_sendMessageFragment, args)
//                }
            }else{
                findNavController().navigate(R.id.loginFragment)
            }
        }


        mBinding.productQuantity.setOnClickListener {
            QuantityPopUp()
        }
    }

    private fun loadPreviews(mediaList: List<Pair<MediaType, String>>) {
        if (mediaList.size < 2) return
        mediaList.forEach {
            when (it.first) {
                MediaType.IMAGE -> {
                    mBinding.previewPane.addImageThumbNail(it.second)
                }
                MediaType.VIDEO -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        mBinding.previewPane.addVideoThumbNail(it.second)
                    }
                }
            }
        }
    }
    fun getRatings(productid : String){
        lifecycleScope.launch {
            val result = RatingRepository.getRatings(productid,2)
            when(result){
                is ResultWrapper.Success ->{
                    Log.d("viewallrating", result.response.toString())

                    val list  = mutableListOf<RatingDTO>()
                    list.addAll(result.response.map { it })
                    ratinglist.value = list
                }
                is ResultWrapper.Failure ->{
                    Log.d("viewallrating", result.errorMessage)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_report_product -> {

                   reportProduct()

                return true


            }
            R.id.menu_seller_details -> {
                val args = bundleOf("user_id" to mProductDetails.userId)
                findNavController().navigate(
                    R.id.action_productDetailsFragment_to_sellerInformationFragment,
                    args
                )
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun shareproduct(message: String){

        // Creating intent with action send
        val intent = Intent(Intent.ACTION_SEND)

        // Setting Intent type
        intent.type = "text/plain"

        // Setting whatsapp package name
        intent.setPackage("com.whatsapp")

        // Give your message here
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val isWhatsappInstalled = whatsappInstalledOrNot()
        // Checking whether whatsapp is installed or not
        if (!isWhatsappInstalled) {
            Toast.makeText(
                requireContext(),
                "Please install whatsapp first.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Starting Whatsapp
        startActivity(intent)
    }

    private fun whatsappInstalledOrNot(): Boolean {
        val pm= activity?.packageManager
        var app_installed = false
        app_installed = try {
            pm?.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {

            false
        }
        return app_installed
    }

    @SuppressLint("SuspiciousIndentation")
    fun productInterestDialog(){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_interst_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext(), R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
          mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)

        btn.setOnClickListener {
            mBinding.showInterestButton.apply {
                alpha = 0.3f
                isEnabled = false
                mProductDetailsViewModel.booleaninterst = false
            }
            mAlertDialog.cancel()
        }
    }

    override fun navigatetoImageDetail() {
        val bundle= Bundle()
        bundle.putString("productID", mProductDetails.productId)
        findNavController().navigate(
            R.id.imageDetail,
            bundle
        )
    }

    private fun reportProduct(){

        if (SharedPrefHelper.isLoggedIn){
            val args = bundleOf("product_id" to mProductDetails.productId)
            findNavController().navigate(
                R.id.action_productDetailsFragment_to_reportProductDialogFragment,
                args
            )
        }else{
            findNavController().navigate(R.id.loginFragment)
        }

    }

    private fun savewishlist() {

        if (SharedPrefHelper.isLoggedIn){
            val userid = SharedPrefHelper.user.userId
            lifecycleScope.launch {
                val product = mProductDetails.productId
                //   showSnackBar("${product}")
                val result = ProductRepository.saveWishList(userid, productId = product)
                when(result){
                    is ResultWrapper.Success ->{

                        /* findNavController().popBackStack(R.id.homeScreenFragment, true)
                         findNavController().navigate(R.id.homeScreenFragment)*/


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

    private fun addtoCartItem(){

        val dialog = BottomSheetDialog(requireContext())

        // on below line we are inflating a layout file which we have created.
        val binding = AddToCartBottomSheetBinding.inflate(LayoutInflater.from(PahadiUncleApplication.instance.applicationContext))

        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)
        bindProgressButton(binding.idBtnDismiss)
        mProductDetailsViewModel.addtoCartPro.observe(viewLifecycleOwner){
            if (it){
                binding.idBtnDismiss.text = "Add to cart"
            }else{
                binding.idBtnDismiss.text = "Proceed to payment"
            }
        }
        val sb = StringBuilder()

         mProductDetailsViewModel.productQuantity.value!!.forEach {
             if (it.isDigit()){
                 sb.append(it)
             }
         }


        binding.addtocartProductQuantity.text = "$sb"
        Picasso.get().load(mProductDetails.mainImageUrl).into(binding.addtocartProductImg)
        binding.addtocartProductName.text = "${mProductDetails.title}"
        binding.addtocartProductPrice.text = "${mProductDetails.priceFormatted}"
        binding.addtocartFinalAmount.text = "Final Amount : ${mProductDetails.priceFormatted}"
         counter = sb.toString().toInt()
        binding.addtocartDismiss.setOnClickListener {
            dialog.dismiss()
        }
        binding.addtocartProductQuantityDeccrease.setOnClickListener {
            if (counter > 1){
                counter = counter-1
                binding.addtocartProductQuantity.text = "$counter"

                val checkamount =  Regex("[^0-9]")
                    .replace(mProductDetails.priceFormatted,"")
                  val newstr = checkamount.replace("₹","")
                 val amount = newstr.toInt() * counter

                binding.addtocartFinalAmount.text = "Final Amount : ₹ $amount /-"
                binding.addtocartProductQuantityIncrease.isClickable = true
            }else{
                binding.addtocartProductQuantityDeccrease.isClickable = false
                binding.addtocartProductQuantityIncrease.isClickable = true

            }
        }


        binding.addtocartProductQuantityIncrease.setOnClickListener {
            if (counter < 10){
                counter = counter+1
                binding.addtocartProductQuantity.text = "$counter"
                val checkamount =  Regex("[^0-9]")
                    .replace(mProductDetails.priceFormatted,"")
                val newstr = checkamount.replace("₹","")
                val amount = newstr.toInt() * counter

                binding.addtocartFinalAmount.text = "Final Amount : ₹ $amount /-"

                binding.addtocartProductQuantityDeccrease.isClickable = true
            }else{
                binding.addtocartProductQuantityIncrease.isClickable = false
                binding.addtocartProductQuantityDeccrease.isClickable = true
            }
        }



        binding.idBtnDismiss.setOnClickListener {
                       if ( mProductDetailsViewModel.addtoCartPro.value == true){

                           binding.idBtnDismiss.apply {
                               showProgress()
                               isClickable = false
                           }
                           lifecycleScope.launch {
                               val result = OrderHistoryRepository.addToCartProduct(mProductDetails.productId, counter.toString())
                               when(result){
                                   is ResultWrapper.Success ->{
                                       temp_showToast("Added to cart")
                                       binding.idBtnDismiss.apply {
                                           hideProgress("Add to cart")
                                           isClickable = true
                                           dialog.dismiss()
                                           counter = 1

                                       }
                                   }
                                   is ResultWrapper.Failure ->{
                                       temp_showToast("Failed to Add cart")
                                       binding.idBtnDismiss.apply {
                                           hideProgress("Add to cart")
                                           isClickable = true
                                           dialog.dismiss()

                                       }
                                   }
                               }
                           }
                       }else{

                           mOrderViewmodel.selectProductlist.add(SelectProdcutDTO(mProductDetails.productId, counter.toString()))
                           mOrderViewmodel.orderamount.value = binding.addtocartFinalAmount.text.toString()
                           val checkamount =  Regex("[^0-9]")
                               .replace(binding.addtocartFinalAmount.text.toString(),"")
                           val newstr = checkamount.replace("₹","")
                           val amount = newstr.toInt() * counter
                           mOrderViewmodel.myPlaceOrderPrice.value = amount
                           findNavController().navigate(R.id.paymentScreen)
                       }
            dialog.dismiss()
        }
        dialog.show()


    }


    private fun QuantityPopUp(){
        val builderSingle = AlertDialog.Builder(requireContext(), com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Alert)

        builderSingle.setTitle("Select Quantity")

        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), R.layout.item_quantity_dailog)
        arrayAdapter.add("1")
        arrayAdapter.add("2")
        arrayAdapter.add("3")
        arrayAdapter.add("4")
        arrayAdapter.add("5")
        arrayAdapter.add("6")
        arrayAdapter.add("7")
        arrayAdapter.add("8")
        arrayAdapter.add("9")
        arrayAdapter.add("10")

        builderSingle.setAdapter(
            arrayAdapter
        ) { dialog, which ->
            val strName = arrayAdapter.getItem(which)

            mProductDetailsViewModel.productQuantity.value = "Qty : $strName"
          //  Toast.makeText(PahadiUncleApplication.instance.applicationContext, "Qty : $strName", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
           /* val builderInner = AlertDialog.Builder(requireContext())
            builderInner.setMessage(strName)
            builderInner.setTitle("Your Selected Item is")
            builderInner.setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
            builderInner.show()*/
        }
        builderSingle.show()
    }
}
