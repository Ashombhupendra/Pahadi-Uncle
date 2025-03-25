package com.pahadi.uncle.presentation.my_products

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.R
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.ProductClickListener
import com.pahadi.uncle.presentation.home.ProductMenuClickListener
import com.pahadi.uncle.presentation.home.ProductsPagingAdapter

class ProductListFragment(
    private val productsLiveData: LiveData<List<ProductDto>>,
    private val productClickListener: ProductClickListener,
    private val productMenuClickListener: ProductMenuClickListener
) : Fragment(R.layout.fragment_product_list) {

    private lateinit var productListRv: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productListRv = view.findViewById(R.id.product_list_rv)
        val no_pro_text = view.findViewById<TextView>(R.id.pro_list_no_pro)
        val productsPagingAdapter =
            ProductsPagingAdapter(productClickListener, productMenuClickListener)
        productListRv.adapter = productsPagingAdapter
        productListRv.layoutManager = GridLayoutManager(requireContext(), 2)

        productsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
               // showSnackBar("No products found")
                   no_pro_text.visibility = View.VISIBLE

                return@observe
            }
            productsPagingAdapter.submitData(lifecycle, PagingData.from(it))
        }
    }


}
