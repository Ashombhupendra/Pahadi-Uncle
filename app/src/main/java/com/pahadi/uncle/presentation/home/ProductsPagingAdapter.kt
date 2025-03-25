
package com.pahadi.uncle.presentation.home

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.categories.ProductViewHolder

class ProductsPagingAdapter(
        private val productClickListener: ProductClickListener,
        private val onMenuItemClickedListener: ProductMenuClickListener? = null,
        private val onFavClicked: ProductFavClick? = null
) : PagingDataAdapter<ProductDto, ProductViewHolder>(itemCallback) {

    companion object {
        val itemCallback = object : DiffUtil.ItemCallback<ProductDto>() {
            override fun areItemsTheSame(oldDto: ProductDto, newDto: ProductDto): Boolean {
                return oldDto == newDto
            }

            override fun areContentsTheSame(oldDto: ProductDto, newDto: ProductDto): Boolean {
                return oldDto.id == newDto.id
            }
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productDto = getItem(position) ?: return
        Log.d("PRODUCTLIST",productDto.toString())
        holder.bind(productDto, productClickListener, onMenuItemClickedListener, onFavClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.create(parent)
    }
}

interface ProductClickListener {
    fun onProductClick(itemView: View, productDto: ProductDto, index: Int)

}

interface ProductMenuClickListener {
    fun onMenuClicked(productDto: ProductDto, menuId: Int)
}

interface ProductFavClick {
    fun onFavClicked(itemView: View, productDto: ProductDto)
}
