package com.pahadi.uncle.presentation.my_add_address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.databinding.ItemAddressListBinding
import com.pahadi.uncle.network.data.UserAddressDTO

class AddressAdapter(val list : List<UserAddressDTO>,val onAddressClick: onAddressClick) :
        RecyclerView.Adapter<AddressAdapter.ViewHolder>(){

    class ViewHolder(val binding : ItemAddressListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(addressDTO: UserAddressDTO, onAddressClick: onAddressClick) {
            binding.itemAddressName.text = addressDTO.full_name
            binding.itemAddressAddress.text = "${addressDTO.flat} , ${addressDTO.landmark} ,${addressDTO.city}, " +
                    "${addressDTO.state} (${addressDTO.pincode})"
            binding.itemAddressPhone.text = addressDTO.mobile

            binding.itemAddressDelete.setOnClickListener {
                onAddressClick.onAddressDelete(addressDTO)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddressListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], onAddressClick)
    }

    override fun getItemCount(): Int {
       return list.size
    }

}

interface onAddressClick{
    fun onAddressDelete(addressDTO: UserAddressDTO)
}
