package com.pahadi.uncle.presentation.home.categories

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.ItemProductBinding
import com.pahadi.uncle.domain.ProductMapper
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.home.ProductClickListener
import com.pahadi.uncle.presentation.home.ProductFavClick
import com.pahadi.uncle.presentation.home.ProductMenuClickListener

class ProductViewHolder(private val binding: ItemProductBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
            productDto: ProductDto,
            productClickListener: ProductClickListener,
            onMenuItemClicked: ProductMenuClickListener? = null,
            onFavClicked: ProductFavClick? = null
    ) {
        binding.product = ProductMapper.toProduct(productDto)

        binding.root.setOnClickListener {
            productClickListener.onProductClick(it, productDto, absoluteAdapterPosition)
        }

        binding.productNofav.isVisible = onFavClicked != null
        binding.productNofav.setOnClickListener {
             if (productDto.wishlist.equals(1)){
                  productDto.wishlist = 0
             }else{
                 productDto.wishlist = 1

             }
                onFavClicked!!.onFavClicked(it, productDto)
        }
        // binding.itemFreelancerRating.isEnabled = false
        if (productDto.ratings.equals(0f)){
           // binding.productRatingCount.visibility = View.GONE
        }
         binding.productRatingCount.text = "(${productDto.total_ratings})"
        binding.menuIv.isVisible = onMenuItemClicked != null
           Log.d("productetail", productDto.toString())
        binding.menuIv.setOnClickListener {
            val context = binding.root.context
            val popup = PopupMenu(context, it)
             if (productDto.activeStatus.toString().equals("0")){
                 popup.apply {
                     inflate(R.menu.product_menu_)
                     val id = this.menu.findItem(R.id.out_of_stock)
                     if (productDto.stock.equals(1)){
                         id.setTitle("In Stock")
                     }else{
                         id.setTitle("Out of Stock")
                     }
                     setOnMenuItemClickListener { menuItem ->

                         onMenuItemClicked?.onMenuClicked(productDto, menuItem.itemId)
                         true
                     }

                     show()
                 }
                 }else{
                 popup.apply {
                     inflate(R.menu.product_menu)
                     val id = this.menu.findItem(R.id.out_of_stock)
                     if (productDto.stock.equals(1)){
                         id.setTitle("In Stock")
                     }else{
                         id.setTitle("Out of Stock")

                     }
                     setOnMenuItemClickListener { menuItem ->

                         onMenuItemClicked?.onMenuClicked(productDto, menuItem.itemId  )
                         Log.d("MENUITEM",menuItem.toString())
                         true
                     }

                     show()

                 }

             }


        }
        itemView.transitionName = "$BASE_URL/uploads/product/${productDto.productImage}"
    }

    companion object {
        fun create(parent: ViewGroup): ProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemProductBinding.inflate(layoutInflater, parent, false)
            return ProductViewHolder(binding)
        }
    }
}
