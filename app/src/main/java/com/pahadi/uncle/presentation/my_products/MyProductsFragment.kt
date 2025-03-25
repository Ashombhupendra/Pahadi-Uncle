package com.pahadi.uncle.presentation.my_products

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.HomeScreenFragment
import com.pahadi.uncle.presentation.home.ProductClickListener
import com.pahadi.uncle.presentation.home.ProductMenuClickListener
import com.pahadi.uncle.presentation.utils.showSnackBar


class MyProductsFragment : Fragment(R.layout.fragment_tabs), ProductClickListener,
    ProductMenuClickListener {
    private val inBed = false

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val viewModel: MyProductsViewModel by viewModels()
    private lateinit var progressIndicator: LinearProgressIndicator
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "pahadi_uncle"
    lateinit var sharedPref: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadMyProducts()
        viewPager = view.findViewById(R.id.view_pager)
        progressIndicator = view.findViewById(R.id.progress_indicator)
        val productsPagerAdapter = ProductsPagerAdapter(this)

        sharedPref = requireContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE)




        viewPager.adapter = productsPagerAdapter
        tabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Approved" else "Unapproved"
        }.attach()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressIndicator.isVisible = isLoading
        }

    }

    inner class ProductsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            val liveData = if (position == 0) viewModel.approvedProducts
            else viewModel.unApprovedProducts
            return ProductListFragment(liveData, this@MyProductsFragment, this@MyProductsFragment)
        }

    }

    override fun onProductClick(itemView: View, productDto: ProductDto, index: Int) {

        val args = bundleOf("ProductDetails" to ProductMapper.toProductDetails(productDto))
        val extras =
            FragmentNavigatorExtras(itemView to "$BASE_URL/uploads/product/${productDto.productImage}")
       findNavController().navigate(R.id.productDetailsFragment, args, null, extras)



    }

    override fun onMenuClicked(productDto: ProductDto, menuId: Int) {
        when (menuId) {
            R.id.delete_product -> {
                val pro_avail_status = SharedPrefHelper.productavailablestatus
                val popup = PopupMenu(context, view)
                 Log.d("PRODUCT_DTO", productDto.toString())

                if (productDto.activeStatus.toString().equals("0")){
                   // showSnackBar("Product Activated")
                    SharedPrefHelper.productavailablestatus = "1"
                    viewModel.updateAvailablestatus(productDto.id.toString(), "1")
                        .observe(viewLifecycleOwner){
                            viewModel.loadMyProducts()
                            productActiveDeactiveDialog("Your Product Deactive")


                        }

                }else{

                //    item.setTitle("De-Activated")
                    SharedPrefHelper.productavailablestatus = "0"
                    viewModel.updateAvailablestatus(productDto.id.toString(), "0")
                        .observe(viewLifecycleOwner){
                            viewModel.loadMyProducts()
                            productActiveDeactiveDialog("Your Product Active")


                        }


                }


            }

    //instead of deleting, product will be activated and deactivated.

            R.id.edit_product -> {
                val productDtoString = Gson().toJson(productDto)
                findNavController().navigate(
                    R.id.sellProductFragment,
                    bundleOf("product_dto" to productDtoString)
                )
            }
            R.id.out_of_stock ->{

                 viewModel.setStockUnstock(productDto.id.toString())
                viewModel.stockcomplete.observe(viewLifecycleOwner){ complete ->
                   if (complete){
                       viewModel.stockcomplete.value = false
                       if (productDto.stock.equals(1)){
                           productActiveDeactiveDialog("Your product successfully added in the stock.")
                       }else{
                           productActiveDeactiveDialog("Now your product is out of stock.")
                       }
                   }

                }


            }
        }
    }


    private fun showProductDeleteDialog(productDto: ProductDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${productDto.productName}")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Delete") { dialog, _ ->
//                viewModel.deleteProduct(productDto.id.toString())
//                    .observe(viewLifecycleOwner) {
//                        viewModel.loadMyProducts()
//                        showSnackBar("Product Deleted Successfully")
//                    }
                dialog.dismiss()
            }.create()
            .show()
    }
    fun productActiveDeactiveDialog(message: String){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_interst_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext(), R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)
        text.setText(message)
        btn.setOnClickListener {
            findNavController().popBackStack(R.id.myProductsFragment, true)
            findNavController().navigate(R.id.myProductsFragment)
            mAlertDialog.cancel()
        }
    }

}
