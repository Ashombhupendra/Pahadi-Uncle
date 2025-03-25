package com.pahadi.uncle.presentation.my_cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.databinding.ItemMycartBinding

import com.pahadi.uncle.network.data.MyCartDTO

class MyCartAdapter(val list : List<MyCartDTO>, val cartClick: onCartClick) :
 RecyclerView.Adapter<MyCartAdapter.ViewHolder>(){
    class ViewHolder(val binding : ItemMycartBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cartDTO: MyCartDTO, cartClick: onCartClick) {
              binding.cartDTO = cartDTO
                  binding.addtocartDelete.setOnClickListener {
                      cartClick.onCartItemClick(cartDTO)
                  }

            binding.addtocartProductPrice.text ="₹ ${cartDTO.price} /-"
            val amount = cartDTO.price.toInt() * cartDTO.quantity.toInt()

            binding.addtocartFinalAmount.text = "Final Amount : ₹ $amount /-"
                  binding.addtocartProductQuantityIncrease.setOnClickListener {

                      var counter = cartDTO.quantity.toInt()
                      if ( counter < 10){
                          counter = counter+1
                          cartDTO.quantity = counter.toString()

                          cartClick.onCartQuantityChange(cartDTO, counter)
                          binding.addtocartProductQuantity.text = "${cartDTO.quantity}"
                          val amount = cartDTO.price.toInt() * counter
                          binding.addtocartFinalAmount.text = "Final Amount : ₹ $amount /-"
                          binding.addtocartProductQuantityDeccrease.isClickable = true

                      }else{
                          binding.addtocartProductQuantityIncrease.isClickable = false
                          binding.addtocartProductQuantityDeccrease.isClickable = true
                      }
                  }
            binding.addtocartProductQuantityDeccrease.setOnClickListener {
                var counter = cartDTO.quantity.toInt()
                if (counter > 1){
                    counter = counter-1
                    cartDTO.quantity = counter.toString()
                    cartClick.onCartQuantityChange(cartDTO, counter)
                    binding.addtocartProductQuantity.text = "${cartDTO.quantity}"


                    val amount = cartDTO.price.toInt() * counter

                    binding.addtocartFinalAmount.text = "Final Amount : ₹ $amount /-"
                    binding.addtocartProductQuantityIncrease.isClickable = true
                }else{
                    binding.addtocartProductQuantityDeccrease.isClickable = false
                    binding.addtocartProductQuantityIncrease.isClickable = true

                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMycartBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position],cartClick)
    }

    override fun getItemCount(): Int {
       return  list.size
    }
}

interface onCartClick{
    fun onCartItemClick(cartDTO: MyCartDTO)

    fun onCartQuantityChange(cartDTO: MyCartDTO, quantity : Int)
}